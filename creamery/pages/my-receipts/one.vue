<template>
  <v-row class="mt-1">
    <v-spacer></v-spacer>
    <v-card id="receipt-card">
      <v-card-text>
        <v-data-table
          :headers="tableHeaders"
          :items="tableItems"
          class="elevation-1"
        >
          <template v-slot:top>
            <v-toolbar flat>
              <v-toolbar-title>Receipt #{{ receiptCode }}</v-toolbar-title>
              <v-divider class="mx-4" inset vertical></v-divider>
              <v-spacer></v-spacer>
              <v-btn
                id="complete-button"
                color="green accent-4"
                dark
                @click="completeReceipt()"
                v-if="
                  status === 'CREATED' &&
                  mode === 'EDIT' &&
                  tableItems.length > 0
                "
              >
                Complete
              </v-btn>
              <v-btn
                id="cancel-button"
                color="red darken-1"
                dark
                class="ml-3"
                @click="cancelReceipt()"
                v-if="
                  (status === 'CREATED' || status === 'COMPLETED') &&
                  mode === 'EDIT'
                "
              >
                Cancel
              </v-btn>
            </v-toolbar>
            <v-row>
              <v-col>
                <v-spacer></v-spacer>
                <v-card id="receipt-fields-card" class="pt-3" elevation="0">
                  <v-card-text>
                    <v-form>
                      <v-text-field
                        label="Created At"
                        v-model="createdTime"
                        readonly
                        dense
                      ></v-text-field>
                      <v-text-field
                        label="Closed At"
                        v-model="checkoutTime"
                        readonly
                        dense
                      ></v-text-field>
                      <v-text-field
                        label="Status"
                        v-model="status"
                        readonly
                        dense
                      ></v-text-field>
                      <v-text-field
                        label="Teller"
                        v-model="tellerName"
                        readonly
                        dense
                      ></v-text-field>
                      <v-text-field
                        label="Total"
                        v-model="sumTotal"
                        readonly
                        dense
                      ></v-text-field>
                    </v-form>
                  </v-card-text>
                </v-card>
                <v-spacer></v-spacer>
              </v-col>
              <v-spacer></v-spacer>
              <v-col>
                <v-spacer></v-spacer>
                <v-img
                  :src="getReceiptImageUrl()"
                  max-width="336"
                  max-height="245"
                  class="mt-6"
                ></v-img>
                <v-spacer></v-spacer>
              </v-col>
              <v-spacer></v-spacer>
            </v-row>
          </template>
          <template v-slot:[`item.code`]="{ item }">
            <div v-if="item.isSaved">{{ item.code }}</div>
            <div v-if="!item.isSaved">
              <v-autocomplete
                v-model="productSelectedByCode"
                :loading="isSearchProductByCodeLoading"
                :items="productsByCode"
                :search-input.sync="searchedProductCode"
                flat
                hide-no-data
                hide-details
                label="find by code"
                solo-inverted
                dense
                item-text="code"
                item-value="code"
                return-object
              >
                <template slot="selection" slot-scope="data">
                  {{ data.item.code }}
                </template>
              </v-autocomplete>
            </div>
          </template>
          <template v-slot:[`item.name`]="{ item }">
            <div v-if="item.isSaved">{{ item.name }}</div>
            <div v-if="!item.isSaved">
              <v-autocomplete
                v-model="productSelectedByName"
                :loading="isSearchProductByNameLoading"
                :items="productsByName"
                :search-input.sync="searchedProductName"
                flat
                hide-no-data
                hide-details
                label="find by name"
                solo-inverted
                dense
                item-text="name"
                item-value="name"
                return-object
              >
                <template slot="selection" slot-scope="data">
                  {{ data.item.name }}
                </template>
              </v-autocomplete>
            </div>
          </template>
          <template v-slot:[`item.actions`]="{ item }">
            <v-icon
              class="ma-1 mr-2"
              color="deep-orange accent-3"
              @click="cancelReceiptItem(item)"
              :disabled="
                status === 'CANCELED' ||
                status === 'COMPLETED' ||
                mode === 'VIEW'
              "
              v-if="isCancelReceiptItemActionVisible()"
            >
              mdi-trash-can-outline
            </v-icon>
          </template>
        </v-data-table>
      </v-card-text>
      <v-card-actions>
        <v-btn id="back-button" class="ml-2" color="primary" @click="onBack()">
          Back to Receipts
        </v-btn>
        <v-spacer></v-spacer>
        <v-btn
          id="add-item-button"
          class="mr-2"
          color="primary"
          @click="onAddReceiptItem()"
        >
          Add Item
        </v-btn>
      </v-card-actions>
    </v-card>
    <v-spacer></v-spacer>
  </v-row>
</template>

<script>
export default {
  data: () => ({
    createdTime: null,
    checkoutTime: null,
    status: null,
    sumTotal: null,
    tellerName: null,
    tableHeaders: [
      {
        text: 'Product Code',
        value: 'code',
        align: 'start',
        sortable: true,
      },
      {
        text: 'Product Name',
        value: 'name',
        align: 'start',
        sortable: true,
      },
      {
        text: 'Amount',
        value: 'amount',
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
        text: 'Cost',
        value: 'cost',
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
    // new item - search product by product code
    searchedProductCode: null,
    isSearchProductByCodeLoading: false,
    productsByCode: [],
    productSelectedByCode: {},
    // new item - search product by name
    searchedProductName: null,
    isSearchProductByNameLoading: false,
    productsByName: [],
    productSelectedByName: {},
  }),

  computed: {
    // ProductsOne page properties
    receiptId: {
      get() {
        return this.$store.state.localStorage.myReceiptsOne.receiptId
      },
    },
    mode: {
      get() {
        return this.$store.state.localStorage.myReceiptsOne.mode
      },
    },
    // computed properties
    receiptCode: {
      get() {
        return this.formatReceiptCode(this.receiptId)
      },
    },
  },

  watch: {
    searchedProductCode(searchedCode) {
      if (searchedCode) {
        this.queryProductsByCode(searchedCode)
      }
    },
    productSelectedByCode(selectedProduct) {
      if (selectedProduct) {
        this.productsByName = this.productsByCode
        this.productSelectedByName = selectedProduct
      }
    },
    searchedProductName(searchedName) {
      if (searchedName) {
        this.queryProductsByName(searchedName)
      }
    },
    productSelectedByName(selectedProduct) {
      if (selectedProduct) {
        this.productsByCode = this.productsByName
        this.productSelectedByCode = selectedProduct
      }
    },
  },

  async created() {
    if (this.mode === 'NEW') {
      this.status = 'CREATED'
      this.tellerName = this.$store.state.localStorage.userName
      this.sumTotal = '0.00'
      return
    }
    await this.loadReceipt(this.receiptId)
  },

  methods: {
    async loadReceipt(receiptId) {
      await this.$http
        .$get('/receipts' + '/' + receiptId)
        .then(this.setReceiptDataIntoPage)
        .catch((error) => {
          // nothing - just show data table without data loaded
          // TODO notify user on errors that might require user action
          console.error(error)
        })
    },
    formatReceiptCode(receiptId) {
      if (!receiptId) {
        return ''
      }
      return receiptId.substring(32).toUpperCase()
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
    formatDateTime(dateTimeStr) {
      if (!dateTimeStr) {
        return ''
      }
      const date = new Date(dateTimeStr)
      const year = date.getFullYear()
      let month = date.getMonth() + 1
      if (month < 10) {
        month = '0' + month
      }
      let day = date.getDate()
      if (day < 10) {
        day = '0' + day
      }
      let hour = date.getHours()
      if (hour < 10) {
        hour = '0' + day
      }
      let minute = date.getMinutes()
      if (minute < 10) {
        minute = '0' + minute
      }
      return year + '-' + month + '-' + day + ' ' + hour + ':' + minute
    },
    calcCost(amount, amountUnit, price) {
      // TODO localize units, handle exceptional cases
      // TODO JS big decimal
      const cost =
        amountUnit === 'UNIT' ? amount * price : (amount * price) / 1000
      return (cost / 100).toFixed(2)
    },
    setReceiptDataIntoPage(receipt) {
      this.createdTime = this.formatDateTime(receipt.createdTime)
      this.checkoutTime = this.formatDateTime(receipt.checkoutTime)
      this.status = receipt.status
      this.sumTotal = '' + (receipt.sumTotal / 100).toFixed(2)
      this.tellerName = receipt.user.username
      this.tableItems = receipt.receiptItems.map((ri) => {
        return {
          id: ri.id,
          code: ri.product.code,
          name: ri.name,
          amount: this.formatAmount(ri.amount, ri.amountUnit),
          price: this.formatPrice(ri.price, ri.amountUnit),
          cost: this.calcCost(ri.amount, ri.amountUnit, ri.price),
          actions: [],
          isSaved: true,
        }
      })
    },
    getStaticRootUrl() {
      return window.location.protocol + '//' + window.location.host
    },
    getReceiptImageUrl() {
      return this.getStaticRootUrl() + '/receipt-jerry.gif'
    },
    onBack() {
      this.$store.commit('localStorage/closeMyReceiptsOne')
      this.$router.push('/my-receipts')
    },
    async completeReceipt() {
      await this.$http
        .$patch('/receipts' + '/' + this.receiptId + '/complete')
        .then(this.setReceiptDataIntoPage)
        .catch((error) => {
          // nothing - just show data table without data loaded
          // TODO notify user on errors that might require user action
          console.error(error)
        })
    },
    async cancelReceipt() {
      await this.$http
        .$patch('/receipts' + '/' + this.receiptId + '/cancel')
        .then(this.setReceiptDataIntoPage)
        .catch((error) => {
          // nothing - just show data table without data loaded
          // TODO notify user on errors that might require user action
          console.error(error)
        })
    },
    async cancelReceiptItem(receiptItem) {
      await this.$http
        .$delete(
          '' +
            '/receipts' +
            '/' +
            this.receiptId +
            '/items' +
            '/' +
            receiptItem.id
        )
        .then((_result) => {
          const indexToDelete = this.tableItems.findIndex(
            (el) => el.id === receiptItem.id
          )
          if (indexToDelete > -1) {
            this.tableItems.splice(indexToDelete, 1)
          }
        })
        .catch((error) => {
          // nothing - just show data table without data loaded
          // TODO notify user on errors that might require user action
          console.error(error)
        })
    },
    isCancelReceiptItemActionVisible() {
      return this.$store.state.localStorage.userRole === 'sr_teller'
    },
    onAddReceiptItem() {
      this.tableItems.push({
        code: null,
        name: null,
        amount: 0,
        price: null,
        cost: 0,
        actions: [],
        isSaved: false,
      })
    },
    async queryProductsByCode(searchedCode) {
      this.isSearchProductByCodeLoading = true
      await this.$http
        .$post('/products/find', {
          codeFilter: searchedCode,
        })
        .then((products) => {
          this.productsByCode = products.map((p) => {
            return {
              code: p.code,
              name: p.name,
              codeAndName: p.code + ' (' + p.name + ')',
            }
          })
          this.isSearchProductByCodeLoading = false
        })
        .catch((error) => {
          // nothing - just show data table without data loaded
          // TODO notify user on errors that might require user action
          console.error(error)
          this.isSearchProductByCodeLoading = false
        })
    },
    async queryProductsByName(searchedName) {
      this.isSearchProductByNameLoading = true
      await this.$http
        .$post('/products/find', {
          nameFilter: searchedName,
        })
        .then((products) => {
          this.productsByName = products.map((p) => {
            return {
              code: p.code,
              name: p.name,
              codeAndName: p.code + ' (' + p.name + ')',
            }
          })
          this.isSearchProductByNameLoading = false
        })
        .catch((error) => {
          // nothing - just show data table without data loaded
          // TODO notify user on errors that might require user action
          console.error(error)
          this.isSearchProductByNameLoading = false
        })
    },
  },
}
</script>

<style>
#receipt-card {
  width: 60em;
}
#receipt-fields-card {
  width: 30em;
}
#back-button {
  width: 14em;
}
#complete-button {
  width: 8em;
}
#cancel-button {
  width: 8em;
}
#add-item-button {
  width: 8em;
}
</style>
