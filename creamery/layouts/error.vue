<template>
  <div class="NotFound">
    <v-container>
      <v-layout row wrap>
        <v-card height="400" color="transparent" flat>
          <div class="display-3 mt-5">Page not found.</div>
          <div class="grey--text lighten-5">
            <br />
            The page you are trying to get to never existed in this reality, or
            has migrated to a parallel universe.
            <br />
          </div>
          <br />
          <v-img max-height="550" max-width="500" src="avatar-error.png">
          </v-img>
          <div class="paragraph-text mt-2">
            <br />Try going back to home page and repeating your action. Or,
            contact helpdesk for support.
            <br />
          </div>
        </v-card>
      </v-layout>
    </v-container>
  </div>
</template>

<style>
.paragraph-text {
  font-size: 18px;
}
</style>
