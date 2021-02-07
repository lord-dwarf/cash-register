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
      this.$http.setHeader('Authorization', null)
      this.$store.commit('setUserRole', null)
      this.$router.push('/')
    })
  },
}
</script>
