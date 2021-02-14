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
          <v-toolbar-title>{{ $t('reports.reports') }}</v-toolbar-title>
          <v-divider class="mx-4" inset vertical></v-divider>
          <v-spacer></v-spacer>
        </v-toolbar>
      </template>
      <template v-slot:[`item.reportName`]="props">
        <v-chip color="primary">
          <div>{{ props.item.reportName }}</div>
        </v-chip>
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
        <v-icon
          class="ma-1"
          color="green accent-4"
          @click="downloadReport(item)"
        >
          mdi-download
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
    tableHeaders: {
      get() {
        return [
          {
            text: this.$t('reports.tableReportName'),
            value: 'reportName',
            align: 'start',
            sortable: true,
          },
          {
            text: this.$t('reports.tableStart'),
            value: 'start',
            align: 'start',
            sortable: true,
          },
          {
            text: this.$t('reports.tableEnd'),
            value: 'end',
            align: 'end',
            sortable: true,
          },
          {
            text: this.$t('reports.tableActions'),
            value: 'actions',
            align: 'start',
            sortable: false,
          },
        ]
      },
    },
  },

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

<style>
#items-per-page {
  width: 8em;
}
</style>
