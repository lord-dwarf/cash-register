<template>
  <v-row class="mt-1">
    <v-spacer></v-spacer>
    <v-card id="login-card">
      <!-- TODO refactor text alignment to center via 'px' -->
      <v-card-title>
        <v-spacer></v-spacer>
        {{ $t('login.welcomeToCreamery') }}
        <v-spacer></v-spacer>
      </v-card-title>
      <v-card-text>
        <v-form v-model="isFormValid">
          <!-- TODO validate user name -->
          <!-- TODO localize validation of password -->
          <!-- TODO space character for password -->
          <!-- TODO consider some validation framework for Vue/Vuetify -->
          <v-text-field
            :label="$t('login.usernameLabel')"
            v-model="username"
            required
            :rules="[(v) => !!v || 'Username is required']"
          ></v-text-field>
          <v-text-field
            :label="$t('login.passwordLabel')"
            v-model="password"
            type="password"
            required
            :rules="[
              (v) => !!v || 'Password is required',
              (v) => (v && v.length >= 8) || 'Password must have 8+ characters',
              (v) =>
                /(?=.*[A-ZА-Я])/.test(v) ||
                'Password must have 1+ uppercase character(s)',
              (v) => /(?=.*\d)/.test(v) || 'Password must have 1+ number(s)',
              (v) =>
                /([!#\\$%\\&\\(\\)*+,\-\\.\\:;<=>\\?@\\[\\\\\]\\^_\`{|}~])/.test(
                  v
                ) || 'Password must have 1+ special character(s)',
            ]"
          ></v-text-field>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <!-- TODO submit login form on pressing ENTER -->
        <v-btn
          id="login-button"
          class="mr-2"
          color="primary"
          :disabled="!isFormValid"
          @click="onLogin()"
        >
          {{ $t('login.signIn') }}
        </v-btn>
        <v-spacer></v-spacer>
      </v-card-actions>
    </v-card>
    <v-spacer></v-spacer>
  </v-row>
</template>

<script>
export default {
  data: () => ({
    username: null,
    password: null,
    isFormValid: true,
  }),
  methods: {
    async onLogin(event) {
      await this.$http
        .$post('/auth/login', {
          login: this.username,
          password: this.password,
        })
        .then((loginResponse) => {
          this.$store.commit('localStorage/setAuthJwt', loginResponse.jwt)
          this.$store.commit(
            'localStorage/setUserRole',
            loginResponse.user.role
          )
          this.$store.commit(
            'localStorage/setUserName',
            loginResponse.user.username
          )
          this.$router.push('/')
          return loginResponse
        })
        .catch((_error) => {
          this.password = null
          return Promise.resolve(null)
        })
    },
  },
}
</script>

<style>
#login-card {
  width: 25em;
}
#login-button {
  width: 8em;
}
</style>
