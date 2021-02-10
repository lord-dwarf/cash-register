<template>
  <v-data-table :headers="tableHeaders" :items="tableItems" class="elevation-1">
    <template v-slot:top>
      <v-toolbar flat>
        <v-toolbar-title>Products</v-toolbar-title>
        <v-divider class="mx-4" inset vertical></v-divider>
        <v-spacer></v-spacer>
        <v-btn
          color="primary"
          dark
          class="mb-2"
          @click="addProduct()"
          v-if="isAddProductButtonVisible()"
        >
          Add Product
        </v-btn>
      </v-toolbar>
    </template>
    <template v-slot:no-data>
      <v-btn color="primary" @click="loadProducts">Reload</v-btn>
    </template>
    <template v-slot:[`item.actions`]="{ item }">
      <v-icon class="ma-1" small @click="viewProduct(item)">mdi-eye</v-icon>
      <v-icon
        class="ma-1 mr-2"
        small
        @click="editProduct(item)"
        v-if="isEditProductActionVisible()"
      >
        mdi-pencil
      </v-icon>
    </template>
  </v-data-table>
</template>

<script>
export default {
  data: () => ({
    tableHeaders: [
      {
        text: 'Code',
        value: 'code',
        align: 'start',
        sortable: true,
      },
      {
        text: 'Name',
        value: 'name',
        align: 'start',
        sortable: true,
      },
      {
        text: 'Category',
        value: 'category',
        align: 'start',
        sortable: true,
      },
      {
        text: 'Price',
        value: 'price',
        align: 'start',
        sortable: true,
      },
      {
        text: 'In Stock',
        value: 'inStock',
        align: 'start',
        sortable: true,
      },
      {
        text: 'Actions',
        value: 'actions',
        align: 'start',
        sortable: false,
      },
    ],
    tableItems: [],
  }),

  async created() {
    await this.loadProducts()
  },

  methods: {
    async loadProducts() {
      await this.$http
        .$get('/products')
        .then((products) => {
          this.tableItems = products.map((p) => {
            return {
              product: p, // capture here, to pass it later to ProductsOne page
              code: p.code,
              name: p.name,
              category: p.category,
              price: this.formatPrice(p.price, p.amountUnit),
              inStock: this.formatAmount(p.amountAvailable, p.amountUnit),
              actions: [],
            }
          })
        })
        .catch((error) => {
          // nothing - just show data table without data loaded
          // TODO notify user on errors that might require user action
          console.error(error)
        })
    },
    formatPrice(price, amountUnit) {
      // TODO localize units, handle exceptional cases
      return amountUnit === 'UNIT'
        ? '' + (price / 100).toFixed(2) + ' /unit'
        : '' + (price / 100).toFixed(2) + ' /kg'
    },
    formatAmount(amount, amountUnit) {
      // TODO localize units, handle exceptional cases
      return amountUnit === 'UNIT' ? amount : (amount / 1000).toFixed(3)
    },
    async viewProduct(item) {
      this.$store.commit('localStorage/viewProductsOne', item.product)
      await this.$router.push('/products/one')
    },
    async editProduct(item) {
      this.$store.commit('localStorage/editProductsOne', item.product)
      await this.$router.push('/products/one')
    },
    async addProduct() {
      this.$store.commit('localStorage/addProductsOne')
      await this.$router.push('/products/one')
    },
    isAddProductButtonVisible() {
      return this.$store.state.localStorage.userRole === 'merch'
    },
    isEditProductActionVisible() {
      return this.$store.state.localStorage.userRole === 'merch'
    },
  },
}
</script>
