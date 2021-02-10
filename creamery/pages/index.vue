<template>
  <div>
    <v-btn
      @click="onTeller()"
      v-if="this.$store.state.localStorage.userRole === 'teller'"
    >
      role teller
    </v-btn>
    <v-btn
      v-if="this.$store.state.localStorage.userRole === 'sr_teller'"
      @click="onSrTeller()"
    >
      role sr_teller
    </v-btn>
    <v-btn
      @click="onMerch()"
      v-if="this.$store.state.localStorage.userRole === 'merch'"
    >
      role merch
    </v-btn>
  </div>
</template>
<script>
export default {
  data: () => ({}),
  methods: {
    async onTeller(event) {
      await this.$http.$post('/receipts', {})
    },
    async onSrTeller(event) {
      const createReceiptResponse = await this.$http.$post('/receipts', {})
      await this.$http.$patch(
        '/receipts' + '/' + createReceiptResponse.id + '/cancel'
      )
    },
    async onMerch(event) {
      await this.$http.$post('/products', {
        code: '123',
        category: 'goat',
        name: 'blue cheese',
        details: '12 months',
        price: 10000,
        amountUnit: 'GRAM',
        amountAvailable: 50000,
      })
    },
  },
}
</script>
