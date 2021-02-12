<template>
  <v-data-table :headers="tableHeaders" :items="tableItems" class="elevation-1">
    <template v-slot:top>
      <v-toolbar flat>
        <v-toolbar-title>Receipts</v-toolbar-title>
        <v-divider class="mx-4" inset vertical></v-divider>
        <div id="toolbar-receipts-all">All</div>
        <v-spacer></v-spacer>
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

  async created() {
    await this.loadReceipts()
  },

  methods: {
    async loadReceipts() {
      return await this.$http
        .$get('/receipts')
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
      this.$store.commit('localStorage/viewReceiptsOne', item.receiptId)
      await this.$router.push('/receipts/one')
    },
    async editReceipt(item) {
      this.$store.commit('localStorage/editReceiptsOne', item.receiptId)
      await this.$router.push('/receipts/one')
    },
  },
}
</script>

<style>
#toolbar-receipts-all {
  font-size: large;
}
</style>
