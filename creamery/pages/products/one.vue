<template>
  <v-row class="mt-1">
    <v-spacer></v-spacer>
    <v-card id="product-card">
      <v-card-text>
        <v-toolbar flat>
          <v-toolbar-title>Product #{{ code }}</v-toolbar-title>
          <v-divider class="mx-4" inset vertical></v-divider>
          <div id="toolbar-product-name">{{ name }}</div>
          <v-spacer></v-spacer>
        </v-toolbar>
        <v-form class="mt-5">
          <v-text-field
            label="Code"
            v-model="code"
            :readonly="mode === 'VIEW'"
          ></v-text-field>
          <v-text-field
            label="Name"
            v-model="name"
            :readonly="mode === 'VIEW'"
          ></v-text-field>
          <v-text-field
            label="Category"
            v-model="category"
            :readonly="mode === 'VIEW'"
          ></v-text-field>
          <v-text-field
            label="Price"
            v-model="price"
            :readonly="mode === 'VIEW'"
          ></v-text-field>
          <v-text-field
            label="In Stock"
            v-model="amountAvailable"
            :readonly="mode === 'VIEW'"
          ></v-text-field>
          <v-text-field
            label="Units"
            v-model="amountUnit"
            :readonly="mode === 'VIEW'"
          ></v-text-field>
          <v-text-field
            label="Details"
            v-model="details"
            :readonly="mode === 'VIEW'"
          ></v-text-field>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
          id="save-button"
          color="primary"
          @click="onSave()"
          v-if="isSaveButtonVisible()"
        >
          Save
        </v-btn>
        <v-btn
          id="back-button"
          class="ml-3 mr-2"
          color="primary"
          @click="onBack()"
        >
          Back to Products
        </v-btn>
      </v-card-actions>
    </v-card>
    <v-spacer></v-spacer>
  </v-row>
</template>

<script>
export default {
  computed: {
    // ProductsOne page properties
    mode: {
      get() {
        return this.$store.state.localStorage.productsOne.mode
      },
    },

    // Product fields
    id: {
      get() {
        return this.$store.state.localStorage.productsOne.product.id
      },
    },
    code: {
      get() {
        return this.$store.state.localStorage.productsOne.product.code
      },
      set(value) {
        this.$store.commit('localStorage/setProductsOneCode', value)
      },
    },
    category: {
      get() {
        return this.$store.state.localStorage.productsOne.product.category
      },
      set(value) {
        this.$store.commit('localStorage/setProductsOneCategory', value)
      },
    },
    name: {
      get() {
        return this.$store.state.localStorage.productsOne.product.name
      },
      set(value) {
        this.$store.commit('localStorage/setProductsOneName', value)
      },
    },
    details: {
      get() {
        return this.$store.state.localStorage.productsOne.product.details
      },
      set(value) {
        this.$store.commit('localStorage/setProductsOneDetails', value)
      },
    },
    price: {
      get() {
        return this.$store.state.localStorage.productsOne.product.price
      },
      set(value) {
        this.$store.commit('localStorage/setProductsOnePrice', value)
      },
    },
    amountAvailable: {
      get() {
        return this.$store.state.localStorage.productsOne.product
          .amountAvailable
      },
      set(value) {
        this.$store.commit('localStorage/setProductsOneAmountAvailable', value)
      },
    },
    amountUnit: {
      get() {
        return this.$store.state.localStorage.productsOne.product.amountUnit
      },
      set(value) {
        this.$store.commit('localStorage/setProductsOneAmountUnit', value)
      },
    },
  },

  created() {
    // perform the following logic only once
    if (this.$store.state.localStorage.productsOne.initialized === true) {
      return
    }

    // recalculate Product fields
    this.price = (this.price / 100).toFixed(2)
    this.amountAvailable =
      this.amountUnit === 'UNIT'
        ? this.amountAvailable
        : (this.amountAvailable / 1000).toFixed(3)

    // set ProductsOne page initialized
    this.$store.commit('localStorage/setProductsOneInitialized')
  },

  methods: {
    isSaveButtonVisible() {
      return (
        this.$store.state.localStorage.userRole === 'merch' &&
        this.mode !== 'VIEW'
      )
    },
    async onSave() {
      if (this.mode === 'EDIT') {
        await this.$http.$put('/products' + '/' + this.id, {
          id: this.id,
          code: this.code,
          category: this.category,
          name: this.name,
          details: this.details,
          price: Math.round(parseFloat(this.price) * 100),
          amountAvailable:
            this.amountUnit === 'UNIT'
              ? parseInt(this.amountAvailable)
              : Math.round(this.amountAvailable * 1000),
          amountUnit: this.amountUnit,
        })
      } else if (this.mode === 'ADD') {
        await this.$http.$post('/products', {
          code: this.code,
          category: this.category,
          name: this.name,
          details: this.details,
          price: Math.round(parseFloat(this.price) * 100),
          amountAvailable:
            this.amountUnit === 'UNIT'
              ? parseInt(this.amountAvailable)
              : Math.round(this.amountAvailable * 1000),
          amountUnit: this.amountUnit,
        })
      }
      this.closePage()
    },
    onBack() {
      this.closePage()
    },
    closePage() {
      this.$router.push('/products')
      this.$store.commit('localStorage/closeProductsOne')
    },
  },
}
</script>

<style>
#product-card {
  width: 40em;
}
#save-button {
  width: 8em;
}
#back-button {
  width: 14em;
}
#toolbar-product-name {
  font-size: large;
}
</style>
