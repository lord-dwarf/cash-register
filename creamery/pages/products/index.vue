<template>
  <div>
    <v-data-table
      :headers="tableHeaders"
      :items="tableItems"
      :page.sync="page"
      :items-per-page="itemsPerPage"
      hide-default-footer
      class="elevation-1"
      @page-count="pageCount = $event"
    >
      <template v-slot:top>
        <v-toolbar flat>
          <v-toolbar-title>{{ $t('products.products') }}</v-toolbar-title>
          <v-divider class="mx-4" inset vertical></v-divider>
          <v-spacer></v-spacer>
          <v-btn
            color="primary"
            dark
            class="mb-2"
            @click="addProduct()"
            v-if="isAddProductButtonVisible()"
          >
            {{ $t('products.addProduct') }}
          </v-btn>
        </v-toolbar>
      </template>
      <template v-slot:no-data>
        <v-btn color="primary" @click="loadProducts">
          {{ $t('componentDataTable.reload') }}
        </v-btn>
      </template>
      <template v-slot:[`item.actions`]="{ item }">
        <v-icon class="ma-1" color="blue accent-1" @click="viewProduct(item)">
          mdi-eye
        </v-icon>
        <v-icon
          class="ma-1 mr-2"
          color="yellow accent-3"
          @click="editProduct(item)"
          v-if="isEditProductActionVisible()"
        >
          mdi-pencil
        </v-icon>
      </template>
    </v-data-table>
    <div class="text-center pt-2">
      <v-row>
        <v-spacer></v-spacer>
        <v-pagination
          v-model="page"
          :length="pageCount"
          class="mt-3"
        ></v-pagination>
        <v-spacer></v-spacer>
        <div id="items-per-page">
          <v-text-field
            :value="itemsPerPage"
            :label="$t('componentDataTable.itemsPerPage')"
            type="number"
            min="3"
            max="20"
            class="mt-6 mr-3"
            dense
            @input="itemsPerPage = parseInt($event, 10)"
          ></v-text-field>
        </div>
      </v-row>
    </div>
  </div>
</template>

<script>
export default {
  data: () => ({
    page: 1,
    pageCount: 0,
    itemsPerPage: 10,
    tableItems: [],
  }),

  computed: {
    userRole: {
      get() {
        return this.$store.state.localStorage.userRole
      },
    },
    tableHeaders: {
      get() {
        return [
          {
            text: this.$t('products.tableCode'),
            value: 'code',
            align: 'start',
            sortable: true,
          },
          {
            text: this.$t('products.tableName'),
            value: 'name',
            align: 'start',
            sortable: true,
          },
          {
            text: this.$t('products.tableCategory'),
            value: 'category',
            align: 'start',
            sortable: true,
          },
          {
            text: this.$t('products.tablePrice'),
            value: 'price',
            align: 'start',
            sortable: true,
          },
          {
            text: this.$t('products.tableInStock'),
            value: 'inStock',
            align: 'start',
            sortable: true,
          },
          {
            text: this.$t('products.tableActions'),
            value: 'actions',
            align: 'start',
            sortable: false,
          },
        ]
      },
    },
  },

  async created() {
    if (this.userRole === null) {
      await this.$router.push('/')
      return
    }
    await this.loadProducts()
  },

  methods: {
    async loadProducts() {
      return await this.$http
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
          return products
        })
        .catch((_error) => {
          // nothing
          return Promise.resolve(null)
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
      return this.userRole === 'merch'
    },
    isEditProductActionVisible() {
      return this.userRole === 'merch'
    },
  },
}
</script>

<style>
#items-per-page {
  width: 8em;
}
</style>
