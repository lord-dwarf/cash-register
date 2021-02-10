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
                v-if="status === 'CREATED' && mode === 'EDIT'"
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
                <v-card id="receipt-fields-card" classa="pa-5" elevation="0">
                  <v-card-text>
                    <v-form>
                      <v-text-field
                        label="Created At"
                        v-model="createdTime"
                        readonly
                      ></v-text-field>
                      <v-text-field
                        label="Closed At"
                        v-model="checkoutTime"
                        readonly
                      ></v-text-field>
                      <v-text-field
                        label="Status"
                        v-model="status"
                        readonly
                      ></v-text-field>
                      <v-text-field
                        label="Teller"
                        v-model="tellerName"
                        readonly
                      ></v-text-field>
                      <v-text-field
                        label="Total"
                        v-model="sumTotal"
                        readonly
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
                  max-width="480"
                  max-height="350"
                  class="mt-6"
                ></v-img>
                <v-spacer></v-spacer>
              </v-col>
              <v-spacer></v-spacer>
            </v-row>
          </template>
          <template v-slot:no-data>
            <v-btn color="primary" @click="loadReceipt()">Reload</v-btn>
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
        <v-spacer></v-spacer>
        <v-btn
          id="back-button"
          class="ml-3 mr-2"
          color="primary"
          @click="onBack()"
        >
          Back to Receipts
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
        .then((r) => {
          this.createdTime = this.formatDateTime(r.createdTime)
          this.checkoutTime = this.formatDateTime(r.checkoutTime)
          this.status = r.status
          this.sumTotal = '' + (r.sumTotal / 100).toFixed(2)
          this.tellerName = r.user.username
          this.tableItems = r.receiptItems.map((ri) => {
            return {
              name: ri.name,
              amount: this.formatAmount(ri.amount, ri.amountUnit),
              price: this.formatPrice(ri.price, ri.amountUnit),
              cost: this.calcCost(ri.amount, ri.amountUnit, ri.price),
              // TODO sum
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
    formatReceiptCode(receiptId) {
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
    onBack() {
      this.$router.push('/receipts')
      this.$store.commit('localStorage/closeReceiptsOne')
    },
    completeReceipt() {
      return ''
    },
    cancelReceipt() {
      return ''
    },
    getReceiptImageUrl() {
      return 'http://localhost:3000/receipt-jerry.gif'
    },
    cancelReceiptItem() {
      return ''
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
</style>
