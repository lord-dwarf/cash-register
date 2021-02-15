<template>
  <v-app dark>
    <v-snackbar v-model="isShowErrorMessage" :timeout="8000" rounded="pill">
      {{ localizeErrorMessageIfNeeded(errorMessage) }}
      <template v-slot:action="{ attrs }">
        <v-btn color="blue" text v-bind="attrs" @click="errorMessage = null">
          {{ $t('default.errorMessageClose') }}
        </v-btn>
      </template>
    </v-snackbar>
    <v-navigation-drawer
      v-model="drawer"
      :mini-variant="miniVariant"
      :clipped="clipped"
      fixed
      app
    >
      <v-list>
        <v-list-item to="/" router exact>
          <v-list-item-action>
            <v-icon>mdi-home</v-icon>
          </v-list-item-action>
          <v-list-item-content>
            <v-list-item-title v-text="$t('default.menuHome')" />
          </v-list-item-content>
        </v-list-item>
        <v-list-item
          to="/products"
          router
          exact
          v-if="this.$store.state.localStorage.userRole !== null"
        >
          <v-list-item-action>
            <v-icon>mdi-cheese</v-icon>
          </v-list-item-action>
          <v-list-item-content>
            <v-list-item-title v-text="$t('default.menuProducts')" />
          </v-list-item-content>
        </v-list-item>
        <v-list-item
          to="/products/one"
          router
          exact
          v-if="isProductsOneVisible()"
        >
          <v-list-item-action>
            <v-icon>mdi-cheese</v-icon>
          </v-list-item-action>
          <v-list-item-content>
            <v-list-item-title
              v-text="
                $t(
                  'default.menu' +
                    this.$store.state.localStorage.productsOne.mode +
                    'Product'
                )
              "
            />
          </v-list-item-content>
        </v-list-item>
        <v-list-item
          to="/receipts"
          router
          exact
          v-if="'sr_teller' === this.$store.state.localStorage.userRole"
        >
          <v-list-item-action>
            <v-icon>mdi-paper-roll-outline</v-icon>
          </v-list-item-action>
          <v-list-item-content>
            <v-list-item-title v-text="$t('default.menuReceipts')" />
          </v-list-item-content>
        </v-list-item>
        <v-list-item
          to="/receipts/one"
          router
          exact
          v-if="isReceiptsOneVisible()"
        >
          <v-list-item-action>
            <v-icon>mdi-paper-roll-outline</v-icon>
          </v-list-item-action>
          <v-list-item-content>
            <v-list-item-title
              v-text="
                $t(
                  'default.menu' +
                    this.$store.state.localStorage.receiptsOne.mode +
                    'Receipt'
                )
              "
            />
          </v-list-item-content>
        </v-list-item>
        <v-list-item
          to="/my-receipts"
          router
          exact
          v-if="
            ['teller', 'sr_teller'].indexOf(
              this.$store.state.localStorage.userRole
            ) > -1
          "
        >
          <v-list-item-action>
            <v-icon>mdi-account-cash-outline</v-icon>
          </v-list-item-action>
          <v-list-item-content>
            <v-list-item-title v-text="$t('default.menuMyReceipts')" />
          </v-list-item-content>
        </v-list-item>
        <v-list-item
          to="/my-receipts/one"
          router
          exact
          v-if="isMyReceiptsOneVisible()"
        >
          <v-list-item-action>
            <v-icon>mdi-account-cash-outline</v-icon>
          </v-list-item-action>
          <v-list-item-content>
            <v-list-item-title
              v-text="
                $t(
                  'default.menu' +
                    this.$store.state.localStorage.myReceiptsOne.mode +
                    'MyReceipt'
                )
              "
            />
          </v-list-item-content>
        </v-list-item>
        <v-list-item
          to="/reports"
          router
          exact
          v-if="this.$store.state.localStorage.userRole === 'sr_teller'"
        >
          <v-list-item-action>
            <v-icon>mdi-file-pdf-box-outline</v-icon>
          </v-list-item-action>
          <v-list-item-content>
            <v-list-item-title v-text="$t('default.menuReports')" />
          </v-list-item-content>
        </v-list-item>
        <v-list-item
          to="/login"
          router
          exact
          v-if="this.$store.state.localStorage.userRole === null"
        >
          <v-list-item-action>
            <v-icon>mdi-login</v-icon>
          </v-list-item-action>
          <v-list-item-content>
            <v-list-item-title v-text="$t('default.menuLogin')" />
          </v-list-item-content>
        </v-list-item>
        <v-list-item
          to="/logout"
          router
          exact
          v-if="this.$store.state.localStorage.userRole !== null"
        >
          <v-list-item-action>
            <v-icon>mdi-logout</v-icon>
          </v-list-item-action>
          <v-list-item-content>
            <v-list-item-title v-text="$t('default.menuLogout')" />
          </v-list-item-content>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>
    <v-app-bar :clipped-left="clipped" fixed app>
      <v-app-bar-nav-icon @click.stop="drawer = !drawer" />
      <v-btn icon @click.stop="miniVariant = !miniVariant">
        <v-icon>mdi-{{ `chevron-${miniVariant ? 'right' : 'left'}` }}</v-icon>
      </v-btn>
      <v-toolbar-title v-text="title" />
      <v-spacer></v-spacer>
      <div>
        {{
          'Shift ' +
          (shiftStatus || 'LOADING') +
          (shiftTime ? ' | ' + shiftTime + '' : '')
        }}
      </div>
      <v-btn
        v-if="shiftStatus === 'INACTIVE'"
        color="green"
        text
        @click="activateShift()"
      >
        Start
      </v-btn>
      <v-divider class="mx-4" inset vertical></v-divider>
      <v-chip id="avatar-name" class="ma-3 pa-4" color="yellow accent-2">
        {{ getUserRoleFriendlyName() }}
      </v-chip>
      <v-avatar size="90">
        <img :src="getUserRoleImage()" />
      </v-avatar>
      <language-input></language-input>
    </v-app-bar>
    <v-main>
      <v-container>
        <nuxt />
      </v-container>
    </v-main>
    <v-footer :absolute="false" app>
      <span
        >&copy;
        {{ new Date().getFullYear() + ' ' + $t('default.copyright') }}</span
      >
    </v-footer>
  </v-app>
</template>

<script>
export default {
  data() {
    return {
      clipped: false,
      drawer: false,
      miniVariant: false,
      shiftTime: null,
    }
  },
  computed: {
    shiftStatus: {
      get() {
        return this.$store.state.shiftStatus
      },
      set(val) {
        this.$store.commit('setShiftStatus', val)
      },
    },
    userRole: {
      get() {
        return this.$store.state.localStorage.userRole
      },
    },
    title: {
      get() {
        return this.$t('default.creamery')
      },
    },
    isShowErrorMessage: {
      get() {
        return !!this.errorMessage
      },
      set(value) {
        this.errorMessage = null
      },
    },
    errorMessage: {
      get() {
        return this.$store.state.localStorage.errorMessage
      },
      set(value) {
        this.$store.commit('localStorage/setErrorMessage', value)
      },
    },
  },
  created() {
    this.pullShiftStatus()
  },
  methods: {
    async pullShiftStatus() {
      setTimeout(this.pullShiftStatus, 12000)
      if (this.userRole !== null) {
        await this.$http
          .$get('/cashbox/shift-status')
          .then((shiftStatus) => {
            this.shiftStatus = shiftStatus.shiftStatus
            this.shiftTime = shiftStatus.shiftStatusElapsedTime
            return shiftStatus
          })
          .catch((_error) => {
            // nothing
            return Promise.resolve(null)
          })
      }
    },
    async activateShift() {
      await this.$http
        .$patch('/cashbox/activate-shift', {})
        .then((shiftStatus) => {
          this.shiftStatus = shiftStatus.shiftStatus
          this.shiftTime = shiftStatus.shiftStatusElapsedTime
          return shiftStatus
        })
        .catch((_error) => {
          // nothing
          return Promise.resolve(null)
        })
    },
    getUserRoleImage() {
      switch (this.userRole) {
        case 'teller':
          return this.getStaticRootUrl() + '/avatar-teller.png'
        case 'sr_teller':
          return this.getStaticRootUrl() + '/avatar-sr_teller.png'
        case 'merch':
          return this.getStaticRootUrl() + '/avatar-merch.png'
        default:
          return this.getStaticRootUrl() + '/avatar-guest.png'
      }
    },
    getStaticRootUrl() {
      return window.location.protocol + '//' + window.location.host
    },
    getUserRoleFriendlyName() {
      switch (this.userRole) {
        case 'teller':
          return this.$t('default.userRoleTeller')
        case 'sr_teller':
          return this.$t('default.userRoleSrTeller')
        case 'merch':
          return this.$t('default.userRoleMerch')
        default:
          return this.$t('default.userRoleGuest')
      }
    },
    isProductsOneVisible() {
      const mode = this.$store.state.localStorage.productsOne.mode
      return (
        (((this.userRole === 'teller' ||
          this.userRole === 'sr_teller' ||
          this.userRole === 'merch') &&
          mode === 'VIEW') ||
          (this.userRole === 'merch' && (mode === 'EDIT' || mode === 'ADD'))) &&
        this.$store.state.localStorage.productsOne.visible
      )
    },
    isReceiptsOneVisible() {
      return (
        this.userRole === 'sr_teller' &&
        this.$store.state.localStorage.receiptsOne.visible
      )
    },
    isMyReceiptsOneVisible() {
      return (
        (this.userRole === 'sr_teller' || this.userRole === 'teller') &&
        this.$store.state.localStorage.myReceiptsOne.visible
      )
    },
    localizeErrorMessageIfNeeded(errorMessage) {
      if (
        errorMessage &&
        errorMessage.startsWith('$') &&
        this.$te(errorMessage.substring(1))
      ) {
        return this.$t(errorMessage.substring(1))
      } else {
        return errorMessage
      }
    },
  },
}
</script>
<style>
#avatar-name {
  color: black;
  font-size: large;
  font-weight: bold;
}
</style>
