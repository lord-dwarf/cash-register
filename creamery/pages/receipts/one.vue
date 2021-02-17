<template>
  <v-row class="mt-1">
    <v-spacer></v-spacer>
    <v-card id="receipt-card">
      <v-card-text>
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
              <v-toolbar-title
                >{{ $t('receiptsOne.receipt') }} #{{ receiptCode }}
              </v-toolbar-title>
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
                {{ $t('receiptsOne.cancel') }}
              </v-btn>
            </v-toolbar>
            <v-row>
              <v-col>
                <v-spacer></v-spacer>
                <v-card id="receipt-fields-card" class="pt-3" elevation="0">
                  <v-card-text>
                    <v-form>
                      <v-text-field
                        :label="$t('receiptsOne.createdTime')"
                        v-model="createdTime"
                        readonly
                        dense
                      ></v-text-field>
                      <v-text-field
                        :label="$t('receiptsOne.checkoutTime')"
                        v-model="checkoutTime"
                        readonly
                        dense
                      ></v-text-field>
                      <v-text-field
                        :label="$t('receiptsOne.status')"
                        v-model="status"
                        readonly
                        dense
                      ></v-text-field>
                      <v-text-field
                        :label="$t('receiptsOne.tellerName')"
                        v-model="tellerName"
                        readonly
                        dense
                      ></v-text-field>
                      <v-text-field
                        :label="$t('receiptsOne.sumTotal')"
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
      </v-card-text>
      <v-card-actions>
        <v-btn id="back-button" class="ml-2" color="primary" @click="onBack()">
          {{ $t('receiptsOne.backToReceipts') }}
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
    page: 1,
    pageCount: 0,
    itemsPerPage: 10,
    createdTime: null,
    checkoutTime: null,
    status: null,
    sumTotal: null,
    tellerName: null,
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
            text: this.$t('receiptsOne.tableProductCode'),
            value: 'code',
            align: 'start',
            sortable: true,
          },
          {
            text: this.$t('receiptsOne.tableProductName'),
            value: 'name',
            align: 'start',
            sortable: true,
          },
          {
            text: this.$t('receiptsOne.tableAmount'),
            value: 'amount',
            align: 'start',
            sortable: true,
          },
          {
            text: this.$t('receiptsOne.tablePrice'),
            value: 'price',
            align: 'start',
            sortable: true,
          },
          {
            text: this.$t('receiptsOne.tableCost'),
            value: 'cost',
            align: 'start',
            sortable: true,
          },
          {
            text: this.$t('receiptsOne.tableActions'),
            value: 'actions',
            align: 'start',
            sortable: false,
          },
        ]
      },
    },
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
    receiptCode: {
      get() {
        return this.formatReceiptCode(this.receiptId)
      },
    },
  },

  async created() {
    if (this.userRole !== 'sr_teller') {
      await this.$router.push('/')
      return
    }
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
        ? parseFloat(price).toFixed(2) + ' /' + this.$t('unit.unit')
        : parseFloat(price).toFixed(2) + ' /' + this.$t('unit.kilo')
    },
    formatAmount(amount, amountUnit) {
      // TODO localize units, handle exceptional cases
      return amountUnit === 'UNIT'
        ? parseFloat(amount).toFixed(2)
        : parseFloat(amount).toFixed(3)
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
    calcCost(amount, price) {
      // TODO JS big decimal
      return (parseFloat(amount) * parseFloat(price)).toFixed(2)
    },
    applyReceipt(receipt) {
      this.createdTime = this.formatDateTime(receipt.createdTime)
      this.checkoutTime = this.formatDateTime(receipt.checkoutTime)
      this.status = receipt.status
      this.sumTotal = receipt.sumTotal.toFixed(2)
      this.tellerName = receipt.user.username
      this.tableItems = receipt.receiptItems.map((ri) => {
        return {
          id: ri.id,
          code: ri.product.code,
          name: ri.name,
          amount: this.formatAmount(ri.amount, ri.amountUnit),
          price: this.formatPrice(ri.price, ri.amountUnit),
          cost: this.calcCost(ri.amount, ri.price),
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
  width: 9em;
}

#items-per-page {
  width: 8em;
}
</style>
