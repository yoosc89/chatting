<script>
  import Main from "./router/Main.svelte";
  import Routes from "svelte-spa-router";
  import "bootstrap/dist/css/bootstrap.min.css";

  const routes = {
    "/": Main,
    "/:": Main,
    "/chat": Main,
  };
</script>

<div>
  <Routes {routes} />
</div>

<style>
</style>
