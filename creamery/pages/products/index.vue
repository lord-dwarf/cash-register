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
          haha
          hoho
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
      <v-icon class="ma-1 mr-2" small @click="editProduct(item)">
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
      const products = await this.$http.$get('/products')
      this.tableItems = products.map((p) => {
        return {
          product: p,
          code: p.code,
          name: p.name,
          category: p.category,
          // TODO localize units, handle exceptional cases
          price:
            p.amountUnit === 'UNIT'
              ? '' + (p.price / 100).toFixed(2) + ' /unit'
              : '' + (p.price / 100).toFixed(2) + ' /kg',
          inStock:
            p.amountUnit === 'UNIT'
              ? p.amountAvailable
              : (p.amountAvailable / 1000).toFixed(3),
          actions: [],
        }
      })
    },
    async viewProduct(item) {
      this.$store.commit('viewProductsOne', item.product)
      await this.$router.push('/products/one')
    },
    async editProduct(item) {
      this.$store.commit('editProductsOne', item.product)
      await this.$router.push('/products/one')
    },
    async addProduct() {
      this.$store.commit('addProductsOne')
      await this.$router.push('/products/one')
    },
  },
}
</script>
