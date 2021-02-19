import colors from 'vuetify/es5/util/colors'
import i18n from './config/i18n'

export default {
  // Disable server-side rendering: https://go.nuxtjs.dev/ssr-mode
  ssr: false,

  // Global page headers: https://go.nuxtjs.dev/config-head
  head: {
    titleTemplate: '%s - creamery',
    title: 'creamery',
    htmlAttrs: {
      lang: 'en',
    },
    meta: [
      { charset: 'utf-8' },
      { name: 'viewport', content: 'width=device-width, initial-scale=1' },
      { hid: 'description', name: 'description', content: '' },
    ],
    link: [{ rel: 'icon', type: 'image/png', href: '/cheese-icon.png' }],
  },

  // Global CSS: https://go.nuxtjs.dev/config-css
  css: [],

  // Plugins to run before rendering page: https://go.nuxtjs.dev/config-plugins
  plugins: [
    '~/plugins/http',
    '~/plugins/vue-cookies',
  ],

  // Auto import components: https://go.nuxtjs.dev/config-components
  components: true,

  // Modules for dev and build (recommended): https://go.nuxtjs.dev/config-modules
  buildModules: [
    // https://go.nuxtjs.dev/eslint
    '@nuxtjs/eslint-module',
    // https://go.nuxtjs.dev/vuetify
    '@nuxtjs/vuetify',
    // Nuxt i18n
    [
      'nuxt-i18n',
      {
        defaultLocale: 'ua',
        locales: [
          {
            code: 'ua',
            name: 'Українська'
          },
          {
            code: 'en',
            name: 'English'
          }
        ],
        vueI18n: i18n,
        strategy: 'no_prefix',
        detectBrowserLanguage: {
          useCookie: true,
          cookieKey: 'cashregister_i18n',
          fallbackLocale: 'ua',
          alwaysRedirect: true,
          onlyOnRoot: false,
          cookieCrossOrigin: false,
          // TODO cookie secure must be true for PROD
          cookieSecure: false,
        },
      },
    ],
  ],

  // Modules: https://go.nuxtjs.dev/config-modules
  modules: [
    'nuxt-vuex-localstorage',
    '@nuxt/http',
  ],

  // Http module configuration: https://http.nuxtjs.org/options
  // TODO base URL must be dependent on env
  http: {
    browserBaseURL: 'http://localhost:8080/api',
    clientTimeout: 5000,
  },

  // Vuetify module configuration: https://go.nuxtjs.dev/config-vuetify
  vuetify: {
    customVariables: ['~/assets/variables.scss'],
    theme: {
      dark: true,
      themes: {
        dark: {
          primary: colors.blue.darken2,
          accent: colors.grey.darken3,
          secondary: colors.amber.darken3,
          info: colors.teal.lighten1,
          warning: colors.amber.base,
          error: colors.deepOrange.accent4,
          success: colors.green.accent3,
        },
      },
    },
  },

  // Build Configuration: https://go.nuxtjs.dev/config-build
  build: {},
}
