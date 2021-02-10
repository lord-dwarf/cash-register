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
    await this.$http.$post('/auth/logout').finally(() => {
      this.$store.commit('localStorage/setAuthJwt', null)
      this.$store.commit('localStorage/setUserRole', null)
      this.$router.push('/')
      this.$store.commit('localStorage/closeProductsOne')
    })
  },
}
</script>
