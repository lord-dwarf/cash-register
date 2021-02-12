<template>
  <v-data-table :headers="tableHeaders" :items="tableItems" class="elevation-1">
    <template v-slot:top>
      <v-toolbar flat>
        <v-toolbar-title>Reports</v-toolbar-title>
        <v-divider class="mx-4" inset vertical></v-divider>
        <v-spacer></v-spacer>
      </v-toolbar>
    </template>
    <template v-slot:[`item.start`]="props">
      <v-edit-dialog :return-value.sync="props.item.start" large persistent>
        <div>{{ props.item.start }}</div>
        <template v-slot:input>
          <div class="mt-4 title">Enter Date</div>
          <v-text-field
            v-model="props.item.start"
            label="Edit"
            single-line
            autofocus
          ></v-text-field>
        </template>
      </v-edit-dialog>
    </template>
    <template v-slot:[`item.end`]="props">
      <v-edit-dialog :return-value.sync="props.item.end" large persistent>
        <div>{{ props.item.end }}</div>
        <template v-slot:input>
          <div class="mt-4 title">Enter Date</div>
          <v-text-field
            v-model="props.item.end"
            label="Edit"
            single-line
            autofocus
          ></v-text-field>
        </template>
      </v-edit-dialog>
    </template>
    <template v-slot:[`item.actions`]="{ item }">
      <v-icon class="ma-1" color="blue accent-1" @click="downloadReport(item)">
        mdi-eye
      </v-icon>
    </template>
  </v-data-table>
</template>

<script>
export default {
  data: () => ({
    tableHeaders: [
      {
        text: 'Report',
        value: 'reportName',
        align: 'start',
        sortable: true,
      },
      {
        text: 'Start',
        value: 'start',
        align: 'start',
        sortable: true,
      },
      {
        text: 'End',
        value: 'end',
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

  created() {
    this.tableItems = [
      {
        reportName: 'Products Sold',
        reportKind: 'products-sold',
        start: this.formatDateBeginOfCurrentMonth(),
        end: this.formatDateEndOfCurrentDay(),
        actions: [],
      },
      {
        reportName: 'Products Not Sold',
        reportKind: 'products-not-sold',
        start: this.formatDateBeginOfCurrentMonth(),
        end: this.formatDateEndOfCurrentDay(),
        actions: [],
      },
    ]
  },

  methods: {
    formatDateBeginOfCurrentMonth() {
      // TODO take initial date from server
      const date = new Date()
      const year = date.getFullYear()
      let month = date.getMonth() + 1
      if (month < 10) {
        month = '0' + month
      }
      const day = '01'
      const hour = '00'
      const minute = '00'
      return year + '-' + month + '-' + day + ' ' + hour + ':' + minute
    },
    formatDateEndOfCurrentDay() {
      // TODO take initial date from server
      const date = new Date()
      const year = date.getFullYear()
      let month = date.getMonth() + 1
      if (month < 10) {
        month = '0' + month
      }
      let day = date.getDate()
      if (day < 10) {
        day = '0' + day
      }
      const hour = 23
      const minute = 59
      return year + '-' + month + '-' + day + ' ' + hour + ':' + minute
    },
    convertDateStringToIsoString(dateString, seconds) {
      // 2012-02-01 00:00
      const date = new Date()
      const year = dateString.substring(0, 4)
      date.setFullYear(parseInt(year))
      const month = dateString.substring(5, 7)
      date.setMonth(parseInt(month) - 1)
      const day = dateString.substring(8, 10)
      date.setDate(parseInt(day))
      const hour = dateString.substring(11, 13)
      date.setHours(parseInt(hour))
      const minute = dateString.substring(14, 16)
      date.setMinutes(parseInt(minute))
      date.setSeconds(seconds)
      // TODO handle errors
      if (seconds === 0) {
        date.setMilliseconds(0)
      } else {
        date.setMilliseconds(999)
      }
      return date.toISOString()
    },
    downloadReport(reportItem) {
      this.$http
        .$post('/reports/' + reportItem.reportKind, {
          start: this.convertDateStringToIsoString(reportItem.start, 0),
          end: this.convertDateStringToIsoString(reportItem.end, 59),
        })
        .then((report) => {
          this.downloadJson(report, reportItem.reportKind)
          return report
        })
        .catch((_error) => {
          // nothing
          return Promise.resolve(null)
        })
    },
    downloadJson(json, reportKind) {
      const a = document.createElement('a')
      const file = new Blob([JSON.stringify(json)], {
        type: 'application/json',
      })
      a.href = URL.createObjectURL(file)
      a.download = reportKind + '.json'
      a.click()
    },
  },
}
</script>
