<template>
  <div id="lang-dropdown" class="mt-6 ml-6">
    <v-row>
      <v-select
        v-model="lang"
        :items="$i18n.locales"
        dense
        solo
        item-text="name"
        item-value="code"
      ></v-select>
    </v-row>
  </div>
</template>

<script>
export default {
  computed: {
    lang: {
      get() {
        let lang = this.$cookies.get('cashregister_i18n')
        lang = lang || 'en'
        return lang
      },
      set(val) {
        this.$cookies.config('365d')
        this.$cookies.set('cashregister_i18n', val)
        this.$i18n.locale = val
      },
    },
  },
}
</script>

<style>
#lang-dropdown {
  width: 8em;
}
</style>
