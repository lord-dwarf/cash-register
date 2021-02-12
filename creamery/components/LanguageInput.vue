<template>
  <v-row v-if="false">
    <v-select
      :items="$i18n.locales"
      label="en"
      dense
      solo
      item-text="code"
      item-value="code"
    ></v-select>
    <div class="lang-dropdown">
      <select v-model="$i18n.locale">
        <option
          v-for="lang in $i18n.locales"
          :key="lang.code"
          :value="lang.code"
        >
          {{ lang.name }}
        </option>
      </select>
    </div>
  </v-row>
</template>

<script>
export default {}
</script>
