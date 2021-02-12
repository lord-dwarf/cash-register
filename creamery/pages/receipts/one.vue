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
  }),

  computed: {
    // ProductsOne page properties
    receiptId: {
      get() {
        return this.$store.state.localStorage.receiptsOne.receiptId
      },
    },
    mode: {
      get() {
        return this.$store.state.localStorage.receiptsOne.mode
      },
    },
    // computed properties
    receiptCode: {
      get() {
        return this.formatReceiptCode(this.receiptId)
      },
    },
  },

  async created() {
    await this.loadReceipt(this.receiptId)
  },

  methods: {
    async loadReceipt(receiptId) {
      await this.$http
        .$get('/receipts' + '/' + receiptId)
        .then(this.applyReceipt)
        .catch((_error) => {
          // nothing
          return Promise.resolve(null)
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
    applyReceipt(receipt) {
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
        }
      })
      return receipt
    },
    getStaticRootUrl() {
      return window.location.protocol + '//' + window.location.host
    },
    getReceiptImageUrl() {
      return this.getStaticRootUrl() + '/receipt-jerry.gif'
    },
    onBack() {
      this.$store.commit('localStorage/closeReceiptsOne')
      this.$router.push('/receipts')
    },
    async cancelReceipt() {
      await this.$http
        .$patch('/receipts' + '/' + this.receiptId + '/cancel')
        .then(this.applyReceipt)
        .catch((_error) => {
          // nothing
          return Promise.resolve(null)
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
        .then(() => {
          const indexToDelete = this.tableItems.findIndex(
            (el) => el.id === receiptItem.id
          )
          if (indexToDelete > -1) {
            this.tableItems.splice(indexToDelete, 1)
          }
          return {}
        })
        .catch((_error) => {
          // nothing
          return Promise.resolve(null)
        })
    },
  },
}
</script>

<style>
#receipt-card {
  width: 70em;
}
#receipt-fields-card {
  width: 30em;
}
#back-button {
  width: 14em;
}
#cancel-button {
  width: 8em;
}
</style>
