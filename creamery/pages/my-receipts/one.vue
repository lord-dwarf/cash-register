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
                >{{ $t('myReceiptsOne.receipt') }} #{{ receiptCode }}
              </v-toolbar-title>
              <v-divider class="mx-4" inset vertical></v-divider>
              <v-spacer></v-spacer>
              <v-btn
                id="complete-button"
                color="green accent-4"
                dark
                @click="completeReceipt()"
                v-if="
                  status === 'CREATED' &&
                  (mode === 'EDIT' || mode === 'NEW') &&
                  tableItems.length > 0
                "
                :disabled="
                  isReceiptItemEditionInProgress() || shiftStatus !== 'ACTIVE'
                "
              >
                {{ $t('myReceiptsOne.complete') }}
              </v-btn>
              <v-btn
                id="cancel-button"
                color="red darken-1"
                dark
                class="ml-3"
                @click="cancelReceipt()"
                v-if="
                  'sr_teller' === userRole &&
                  (status === 'CREATED' || status === 'COMPLETED') &&
                  (mode === 'EDIT' || mode === 'NEW') &&
                  tableItems.length > 0
                "
                :disabled="
                  isReceiptItemEditionInProgress() || shiftStatus !== 'ACTIVE'
                "
              >
                {{ $t('myReceiptsOne.cancel') }}
              </v-btn>
            </v-toolbar>
            <v-row>
              <v-col>
                <v-spacer></v-spacer>
                <v-card id="receipt-fields-card" class="pt-3" elevation="0">
                  <v-card-text>
                    <v-form>
                      <v-text-field
                        :label="$t('myReceiptsOne.createdTime')"
                        v-model="createdTime"
                        readonly
                        dense
                      ></v-text-field>
                      <v-text-field
                        :label="$t('myReceiptsOne.checkoutTime')"
                        v-model="checkoutTime"
                        readonly
                        dense
                      ></v-text-field>
                      <v-text-field
                        :label="$t('myReceiptsOne.status')"
                        v-model="status"
                        readonly
                        dense
                      ></v-text-field>
                      <v-text-field
                        :label="$t('myReceiptsOne.tellerName')"
                        v-model="tellerName"
                        readonly
                        dense
                      ></v-text-field>
                      <v-text-field
                        :label="$t('myReceiptsOne.sumTotal')"
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
            <div v-if="item.isSaved || item.code">{{ item.code }}</div>
            <div v-if="!item.isSaved && !item.code">
              <v-autocomplete
                v-model="productSelectedByCode"
                :loading="isSearchProductByCodeLoading"
                :items="productsByCode"
                :search-input.sync="searchedProductCode"
                flat
                hide-no-data
                hide-details
                :label="$t('myReceiptsOne.findByCode')"
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
            <div v-if="item.isSaved || item.name">{{ item.name }}</div>
            <div v-if="!item.isSaved && !item.name">
              <v-autocomplete
                v-model="productSelectedByName"
                :loading="isSearchProductByNameLoading"
                :items="productsByName"
                :search-input.sync="searchedProductName"
                flat
                hide-no-data
                hide-details
                :label="$t('myReceiptsOne.findByName')"
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
          <template v-slot:[`item.amount`]="props">
            <div v-if="props.item.isSaved">
              {{ props.item.amount }}
            </div>
            <div v-if="!props.item.isSaved">
              <v-edit-dialog
                :return-value.sync="props.item.amount"
                large
                persistent
                :save-text="$t('myReceiptsOne.save')"
                :cancel-text="$t('myReceiptsOne.cancel')"
              >
                <div>{{ props.item.amount }}</div>
                <template v-slot:input>
                  <div class="mt-4 title">
                    {{ $t('myReceiptsOne.enterAmount') }}
                  </div>
                  <v-text-field
                    v-model="props.item.amount"
                    :label="$t('myReceiptsOne.amount')"
                    single-line
                    autofocus
                  ></v-text-field>
                </template>
              </v-edit-dialog>
            </div>
          </template>
          <template v-slot:[`item.price`]="{ item }">
            <div>{{ item.price }}</div>
          </template>
          <template v-slot:[`item.actions`]="{ item }">
            <v-icon
              class="ma-1"
              color="yellow accent-3"
              @click="editReceiptItem(item)"
              :disabled="
                status === 'CANCELED' ||
                status === 'COMPLETED' ||
                mode === 'VIEW' ||
                isReceiptItemEditionInProgress()
              "
              v-if="isEditReceiptItemActionVisible(item)"
            >
              mdi-pencil
            </v-icon>
            <v-icon
              class="ma-1 mr-2"
              color="green accent-4"
              @click="saveReceiptItem(item)"
              :disabled="
                status === 'CANCELED' ||
                status === 'COMPLETED' ||
                mode === 'VIEW' ||
                item.amount <= 0 ||
                !item.productId
              "
              v-if="isSaveReceiptItemActionVisible(item)"
            >
              mdi-check-outline
            </v-icon>
            <v-icon
              class="ma-1 mr-2"
              color="deep-orange accent-3"
              @click="cancelReceiptItem(item)"
              :disabled="
                status === 'CANCELED' ||
                status === 'COMPLETED' ||
                mode === 'VIEW' ||
                isReceiptItemEditionInProgress()
              "
              v-if="isCancelReceiptItemActionVisible(item)"
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
          {{ $t('myReceiptsOne.backToMyReceipts') }}
        </v-btn>
        <v-spacer></v-spacer>
        <v-btn
          id="add-item-button"
          class="mr-2"
          color="primary"
          @click="onAddReceiptItem()"
          v-if="status === 'CREATED' && (mode === 'EDIT' || mode === 'NEW')"
          :disabled="
            isReceiptItemEditionInProgress() || shiftStatus !== 'ACTIVE'
          "
        >
          {{ $t('myReceiptsOne.addItem') }}
        </v-btn>
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
    userRole: {
      get() {
        return this.$store.state.localStorage.userRole
      },
    },
    shiftStatus: {
      get() {
        return this.$store.state.shiftStatus
      },
    },
    tableHeaders: {
      get() {
        return [
          {
            text: this.$t('myReceiptsOne.tableProductCode'),
            value: 'code',
            align: 'start',
            sortable: true,
          },
          {
            text: this.$t('myReceiptsOne.tableProductName'),
            value: 'name',
            align: 'start',
            sortable: true,
          },
          {
            text: this.$t('myReceiptsOne.tableAmount'),
            value: 'amount',
            align: 'start',
            sortable: true,
          },
          {
            text: this.$t('myReceiptsOne.tablePrice'),
            value: 'price',
            align: 'start',
            sortable: true,
          },
          {
            text: this.$t('myReceiptsOne.tableCost'),
            value: 'cost',
            align: 'start',
            sortable: true,
          },
          {
            text: this.$t('myReceiptsOne.tableActions'),
            value: 'actions',
            align: 'start',
            sortable: false,
          },
        ]
      },
    },
    receiptId: {
      get() {
        return this.$store.state.localStorage.myReceiptsOne.receiptId
      },
      set(val) {
        this.$store.commit('localStorage/setMyReceiptsOneReceiptId', val)
      },
    },
    mode: {
      get() {
        return this.$store.state.localStorage.myReceiptsOne.mode
      },
    },
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
        const newReceiptItem = this.tableItems[this.tableItems.length - 1]
        newReceiptItem.price = this.formatPrice(
          selectedProduct.price,
          selectedProduct.amountUnit
        )
        newReceiptItem.amount = '0'
        newReceiptItem.amountUnit = selectedProduct.amountUnit
        newReceiptItem.productId = selectedProduct.id
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
        const newReceiptItem = this.tableItems[this.tableItems.length - 1]
        newReceiptItem.price = this.formatPrice(
          selectedProduct.price,
          selectedProduct.amountUnit
        )
        newReceiptItem.amount = '0'
        newReceiptItem.amountUnit = selectedProduct.amountUnit
        newReceiptItem.productId = selectedProduct.id
      }
    },
  },

  async created() {
    if (!(this.userRole === 'teller' || this.userRole === 'sr_teller')) {
      await this.$router.push('/')
      return
    }
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
        ? parseFloat(amount).toFixed(0)
        : parseFloat(amount).toFixed(3)
    },
    decodeAmount(amount, amountUnit) {
      // TODO localize units, handle exceptional cases
      const decodedAmount =
        amountUnit === 'UNIT'
          ? parseFloat(amount).toFixed(0)
          : parseFloat(amount).toFixed(3)
      return decodedAmount
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
    setReceiptDataIntoPage(receipt) {
      this.createdTime = this.formatDateTime(receipt.createdTime)
      this.checkoutTime = this.formatDateTime(receipt.checkoutTime)
      this.status = receipt.status
      this.sumTotal = parseFloat(receipt.sumTotal).toFixed(2)
      this.tellerName = receipt.user.username
      this.tableItems = receipt.receiptItems.map((ri) => {
        const calcCostFun = this.calcCost
        return {
          id: ri.id,
          productId: ri.product.id,
          code: ri.product.code,
          name: ri.name,
          amount: this.formatAmount(ri.amount, ri.amountUnit),
          amountUnit: ri.amountUnit,
          price: this.formatPrice(ri.price, ri.amountUnit),
          cost: calcCostFun(ri.amount, ri.price),
          actions: [],
          isSaved: true,
        }
      })
      return {}
    },
    getStaticRootUrl() {
      return window.location.protocol + '//' + window.location.host
    },
    getReceiptImageUrl() {
      return this.getStaticRootUrl() + '/receipt-jerry.gif'
    },
    onBack() {
      this.closePage()
    },
    closePage() {
      this.$store.commit('localStorage/closeMyReceiptsOne')
      this.$router.push('/my-receipts')
    },
    async completeReceipt() {
      await this.$http
        .$patch('/receipts' + '/' + this.receiptId + '/complete')
        .then(this.setReceiptDataIntoPage)
        .catch((_error) => {
          // nothing
          return Promise.resolve(null)
        })
    },
    async cancelReceipt() {
      await this.$http
        .$patch('/receipts' + '/' + this.receiptId + '/cancel')
        .then(this.setReceiptDataIntoPage)
        .catch((_error) => {
          // nothing
          return Promise.resolve(null)
        })
    },
    async cancelReceiptItem(receiptItem) {
      await this.$http
        .$delete(
          '/receipts' + '/' + this.receiptId + '/items' + '/' + receiptItem.id
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
    editReceiptItem(receiptItem) {
      receiptItem.isSaved = false
    },
    async saveReceiptItem(receiptItem) {
      if (!this.receiptId) {
        const receipt = await this.$http
          .$post('/receipts', {})
          .then((receipt) => {
            return Promise.resolve(receipt)
          })
          .catch((_error) => {
            // nothing
            return Promise.resolve(null)
          })
        if (!receipt) {
          return
        }
        this.receiptId = receipt.id
      }
      if (receiptItem.id) {
        this.$http
          .$patch(
            '/receipts' +
              '/' +
              this.receiptId +
              '/items' +
              '/' +
              receiptItem.id,
            {
              amount: this.decodeAmount(
                receiptItem.amount,
                receiptItem.amountUnit
              ),
            }
          )
          .then(async (_result) => {
            receiptItem.isSaved = true
            this.cleanSearchProductFields()
            return await this.loadReceipt(this.receiptId)
          })
          .catch((_error) => {
            // nothing
            return Promise.resolve(null)
          })
      } else {
        this.$http
          .$post('/receipts' + '/' + this.receiptId + '/items', {
            productId: receiptItem.productId,
            receiptItemAmount: this.decodeAmount(
              receiptItem.amount,
              receiptItem.amountUnit
            ),
          })
          .then(async (_result) => {
            receiptItem.isSaved = true
            this.cleanSearchProductFields()
            return await this.loadReceipt(this.receiptId)
          })
          .catch((_error) => {
            // nothing
            return Promise.resolve(null)
          })
      }
    },
    isEditReceiptItemActionVisible(item) {
      return item.isSaved
    },
    isSaveReceiptItemActionVisible(item) {
      return !item.isSaved
    },
    isCancelReceiptItemActionVisible(item) {
      return this.userRole === 'sr_teller' && item.isSaved
    },
    isReceiptItemEditionInProgress() {
      return !!this.tableItems.find((ri) => !ri.isSaved)
    },
    onAddReceiptItem() {
      if (this.tableItems.find((ri) => !ri.isSaved)) {
        return
      }
      this.tableItems.push({
        code: null,
        name: null,
        amount: 0,
        price: '-',
        cost: '-',
        actions: [],
        isSaved: false,
      })
    },
    async queryProductsByCode(searchedCode) {
      this.isSearchProductByCodeLoading = true
      await this.$http
        .$post('/products/find', {
          filterKind: 'CODE',
          filterValue: searchedCode,
        })
        .then((products) => {
          this.productsByCode = products.map((p) => {
            // TODO use codeAndName to display select items
            return {
              id: p.id,
              code: p.code,
              name: p.name,
              codeAndName: p.code + ' (' + p.name + ')',
              price: p.price,
              amountUnit: p.amountUnit,
            }
          })
          this.isSearchProductByCodeLoading = false
          return products
        })
        .catch((_error) => {
          this.isSearchProductByCodeLoading = false
          return Promise.resolve(null)
        })
    },
    async queryProductsByName(searchedName) {
      this.isSearchProductByNameLoading = true
      await this.$http
        .$post('/products/find', {
          filterKind: 'NAME',
          filterValue: searchedName,
        })
        .then((products) => {
          this.productsByName = products.map((p) => {
            // TODO use codeAndName to display select items
            return {
              id: p.id,
              code: p.code,
              name: p.name,
              codeAndName: p.code + ' (' + p.name + ')',
              price: p.price,
              amountUnit: p.amountUnit,
            }
          })
          this.isSearchProductByNameLoading = false
          return products
        })
        .catch((_error) => {
          this.isSearchProductByNameLoading = false
          return Promise.resolve(null)
        })
    },
    cleanSearchProductFields() {
      this.searchedProductCode = null
      this.isSearchProductByCodeLoading = false
      this.productsByCode = []
      this.productSelectedByCode = {}
      this.searchedProductName = null
      this.isSearchProductByNameLoading = false
      this.productsByName = []
      this.productSelectedByName = {}
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

#complete-button {
  width: 9em;
}

#cancel-button {
  width: 9em;
}

#add-item-button {
  width: 12em;
}

#items-per-page {
  width: 8em;
}
</style>
