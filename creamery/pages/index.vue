<template>
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
</template>
<script>
export default {
  data: () => ({}),
  methods: {
    getStaticRootUrl() {
      return window.location.protocol + '//' + window.location.host
    },
    getReceiptImageUrl() {
      return this.getStaticRootUrl() + '/product-guy.gif'
    },
  },
}
</script>
