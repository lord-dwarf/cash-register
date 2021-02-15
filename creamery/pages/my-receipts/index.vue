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
          <v-toolbar-title>{{ $t('myReceipts.myReceipts') }}</v-toolbar-title>
          <v-divider class="mx-4" inset vertical></v-divider>
          <div id="toolbar-receipts-username">
            {{ userName }}
          </div>
          <v-spacer></v-spacer>
          <v-btn
            id="new-receipt-button"
            color="primary"
            :disabled="shiftStatus !== 'ACTIVE'"
            @click="newReceipt()"
          >
            {{ $t('myReceipts.newReceipt') }}
          </v-btn>
        </v-toolbar>
      </template>
      <template v-slot:no-data>
        <v-btn color="primary" @click="loadReceipts()">
          {{ $t('componentDataTable.reload') }}
        </v-btn>
      </template>
      <template v-slot:[`item.actions`]="{ item }">
        <v-icon class="ma-1" color="blue accent-1" @click="viewReceipt(item)">
          mdi-eye
        </v-icon>
        <v-icon
          class="ma-1"
          color="yellow accent-3"
          @click="editReceipt(item)"
          :disabled="
            item.status === 'CANCELED' ||
            (item.status === 'COMPLETED' && 'teller' === userRole)
          "
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
    userName: {
      get() {
        return this.$store.state.localStorage.userName
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
            text: this.$t('myReceipts.tableReceiptCode'),
            value: 'receiptCode',
            align: 'start',
            sortable: true,
          },
          {
            text: this.$t('myReceipts.tableCreatedTime'),
            value: 'createdTime',
            align: 'start',
            sortable: true,
          },
          {
            text: this.$t('myReceipts.tableCheckoutTime'),
            value: 'checkoutTime',
            align: 'start',
            sortable: true,
          },
          {
            text: this.$t('myReceipts.tableStatus'),
            value: 'status',
            align: 'start',
            sortable: true,
          },
          {
            text: this.$t('myReceipts.tableTellerName'),
            value: 'tellerName',
            align: 'start',
            sortable: true,
          },
          {
            text: this.$t('myReceipts.tableSumTotal'),
            value: 'sumTotal',
            align: 'start',
            sortable: true,
          },
          {
            text: this.$t('myReceipts.tableActions'),
            value: 'actions',
            align: 'start',
            sortable: false,
          },
        ]
      },
    },
  },

  async created() {
    if (!(this.userRole === 'teller' || this.userRole === 'sr_teller')) {
      await this.$router.push('/')
      return
    }
    await this.loadReceipts()
  },

  methods: {
    async loadReceipts() {
      await this.$http
        .$get('/receipts/by-teller')
        .then((receipts) => {
          this.tableItems = receipts.map((r) => {
            return {
              receiptId: r.id,
              receiptCode: this.formatReceiptCode(r.id),
              createdTime: this.formatDateTime(r.createdTime),
              checkoutTime: this.formatDateTime(r.checkoutTime),
              status: r.status,
              sumTotal: '' + (r.sumTotal / 100).toFixed(2),
              tellerName: r.user.username,
              actions: [],
            }
          })
          return receipts
        })
        .catch((_error) => {
          // nothing
          return Promise.resolve(null)
        })
    },
    formatReceiptCode(receiptId) {
      return receiptId.substring(32).toUpperCase()
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
    async viewReceipt(item) {
      this.$store.commit('localStorage/viewMyReceiptsOne', item.receiptId)
      await this.$router.push('/my-receipts/one')
    },
    async editReceipt(item) {
      this.$store.commit('localStorage/editMyReceiptsOne', item.receiptId)
      await this.$router.push('/my-receipts/one')
    },
    async newReceipt() {
      this.$store.commit('localStorage/newMyReceiptsOne')
      await this.$router.push('/my-receipts/one')
    },
  },
}
</script>

<style>
#toolbar-receipts-username {
  font-size: large;
}

#items-per-page {
  width: 8em;
}
</style>
