<template>
  <v-app dark>
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
            <v-list-item-title v-text="'Home'" />
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
            <v-list-item-title v-text="'Products'" />
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
                this.$store.state.localStorage.productsOne.mode + ' Product'
              "
            />
          </v-list-item-content>
        </v-list-item>
        <v-list-item
          to="/receipts"
          router
          exact
          v-if="
            ['teller', 'sr_teller'].indexOf(
              this.$store.state.localStorage.userRole
            ) > -1
          "
        >
          <v-list-item-action>
            <v-icon>mdi-paper-roll-outline</v-icon>
          </v-list-item-action>
          <v-list-item-content>
            <v-list-item-title v-text="'Receipts'" />
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
                this.$store.state.localStorage.receiptsOne.mode + ' Receipt'
              "
            />
          </v-list-item-content>
        </v-list-item>
        <v-list-item
          to="/reports"
          router
          exact
          v-if="this.$store.state.localStorage.userRole === 'merch'"
        >
          <v-list-item-action>
            <v-icon>mdi-file-pdf-box-outline</v-icon>
          </v-list-item-action>
          <v-list-item-content>
            <v-list-item-title v-text="'Reports'" />
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
            <v-list-item-title v-text="'Login'" />
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
            <v-list-item-title v-text="'Logout'" />
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
      <v-chip id="avatar-name" class="ma-3 pa-4" color="yellow lighten">
        {{ getUserRoleFriendlyName() }}
      </v-chip>
      <v-avatar size="85">
        <img :src="getUserRoleImage()" />
      </v-avatar>
    </v-app-bar>
    <v-main>
      <v-container>
        <nuxt />
      </v-container>
    </v-main>
    <v-footer :absolute="false" app>
      <span>&copy; {{ new Date().getFullYear() }}</span>
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
      title: 'Creamery',
    }
  },
  methods: {
    getUserRoleImage() {
      switch (this.$store.state.localStorage.userRole) {
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
      switch (this.$store.state.localStorage.userRole) {
        case 'teller':
          return 'Teller'
        case 'sr_teller':
          return 'Sr. Teller'
        case 'merch':
          return 'Merchandiser'
        default:
          return 'Guest'
      }
    },
    isProductsOneVisible() {
      const role = this.$store.state.localStorage.userRole
      const mode = this.$store.state.localStorage.productsOne.mode
      return (
        (((role === 'teller' || role === 'sr_teller' || role === 'merch') &&
          mode === 'VIEW') ||
          (role === 'merch' && (mode === 'EDIT' || mode === 'ADD'))) &&
        this.$store.state.localStorage.productsOne.visible
      )
    },
    isReceiptsOneVisible() {
      const role = this.$store.state.localStorage.userRole
      return (
        role === 'sr_teller' &&
        this.$store.state.localStorage.receiptsOne.visible
      )
    },
  },
}
</script>
<style>
#avatar-name {
  color: black;
  font-size: large;
}
</style>
