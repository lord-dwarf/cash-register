<template>
  <v-col>
    <v-spacer></v-spacer>
    <v-img
      :src="getReceiptImageUrl()"
      max-width="720"
      max-height="525"
      class="mt-6"
    ></v-img>
    <v-spacer></v-spacer>
  </v-col>
</template>
<script>
export default {
  data: () => ({}),
  methods: {
    getStaticRootUrl() {
      return window.location.protocol + '//' + window.location.host
    },
    getReceiptImageUrl() {
      return this.getStaticRootUrl() + '/cash-register-bw.gif'
    },
  },
}
</script>
