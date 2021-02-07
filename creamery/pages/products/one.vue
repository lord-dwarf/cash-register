<template>
  <v-row class="mt-1">
    <v-spacer></v-spacer>
    <v-card id="product-card">
      <v-card-text>
        <v-form>
          <v-text-field
            label="Code"
            v-model="product.code"
            :readonly="readonly"
          ></v-text-field>
          <v-text-field
            label="Category"
            v-model="product.category"
            :readonly="readonly"
          ></v-text-field>
          <v-text-field
            label="Name"
            v-model="product.name"
            :readonly="readonly"
          ></v-text-field>
          <v-text-field
            label="Details"
            v-model="product.details"
            :readonly="readonly"
          ></v-text-field>
          <v-text-field
            label="Price"
            v-model="product.price"
            :readonly="readonly"
          ></v-text-field>
          <v-text-field
            label="In Stock"
            v-model="product.amountAvailable"
            :readonly="readonly"
          ></v-text-field>
          <v-text-field
            label="Units"
            v-model="product.amountUnit"
            :readonly="readonly"
          ></v-text-field>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" @click="onSaveOrBack">
          {{ getFormButtonText() }}
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
    product: null,
    readonly: null,
  }),

  created() {
    this.product = this.$store.state.productsOne.product
    this.product.price = (this.product.price / 100).toFixed(2)
    this.product.amountAvailable =
      this.product.amountUnit === 'UNIT'
        ? this.product.amountAvailable
        : (this.product.amountAvailable / 1000).toFixed(3)
    this.readonly = this.$store.state.productsOne.readonly
  },

  methods: {
    getFormButtonText() {
      if (this.readonly) {
        return 'Back to Products'
      } else {
        return 'Save'
      }
    },
    onSaveOrBack() {
      if (this.$store.state.productsOne.editable) {
        // TODO
      }
      this.$router.push('/products')
    },
  },
}
</script>

<style>
#product-card {
  width: 40em;
}
</style>
