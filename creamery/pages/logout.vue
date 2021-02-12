<template>
  <div></div>
</template>

<script>
export default {
  data: () => ({
    username: null,
    password: null,
    isFormValid: true,
  }),
  async created() {
    await this.$http
      .$post('/auth/logout')
      .catch((_error) => {
        // nothing
        return Promise.resolve(null)
      })
      .finally(() => {
        this.$store.commit('localStorage/setAuthJwt', null)
        this.$store.commit('localStorage/setUserRole', null)
        this.$store.commit('localStorage/setUserName', null)
        this.$router.push('/')
        this.$store.commit('localStorage/closeProductsOne')
        this.$store.commit('localStorage/closeReceiptsOne')
        this.$store.commit('localStorage/closeMyReceiptsOne')
      })
  },
}
</script>
