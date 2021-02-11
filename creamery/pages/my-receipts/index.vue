<template>
  <v-data-table :headers="tableHeaders" :items="tableItems" class="elevation-1">
    <template v-slot:top>
      <v-toolbar flat>
        <v-toolbar-title>My Receipts</v-toolbar-title>
        <v-divider class="mx-4" inset vertical></v-divider>
        <div id="toolbar-receipts-username">
          {{ userName }}
        </div>
        <v-spacer></v-spacer>
        <v-btn
          id="new-receipt-button"
          color="primary"
          dark
          @click="newReceipt()"
        >
          New Receipt
        </v-btn>
      </v-toolbar>
    </template>
    <template v-slot:no-data>
      <v-btn color="primary" @click="loadReceipts()">Reload</v-btn>
    </template>
    <template v-slot:[`item.actions`]="{ item }">
      <v-icon class="ma-1" color="blue accent-1" @click="viewReceipt(item)">
        mdi-eye
      </v-icon>
      <v-icon
        class="ma-1"
        color="yellow accent-3"
        @click="editReceipt(item)"
        :disabled="item.status === 'CANCELED'"
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
        value: 'receiptCode',
        align: 'start',
        sortable: true,
      },
      {
        text: 'Created At',
        value: 'createdTime',
        align: 'start',
        sortable: true,
      },
      {
        text: 'Closed At',
        value: 'checkoutTime',
        align: 'start',
        sortable: true,
      },
      {
        text: 'Status',
        value: 'status',
        align: 'start',
        sortable: true,
      },
      {
        text: 'Teller',
        value: 'tellerId',
        align: 'start',
        sortable: true,
      },
      {
        text: 'Total',
        value: 'sumTotal',
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
    userName: {
      get() {
        return this.$store.state.localStorage.userName
      },
    },
  },

  async created() {
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
              tellerId: r.user.username,
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
</style>
