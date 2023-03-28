<script>
  import { onDestroy, onMount } from "svelte";
  import moment from "moment/min/moment-with-locales";
  moment.locale("ko");

  const urlPramms = location.hash.split("?")[1].split("&");

  const channel = urlPramms.filter((e) => e.match(/channel/))[0].replace("channel=", "");
  const username = urlPramms.filter((e) => e.match(/username/))[0].replace("username=", "");

  const server = "ws://localhost:9095/api/ws/" + channel + "/" + username;
  const ws = new WebSocket(server);
  $: message = "";
  let messagebox = [];
  onMount(() => {
    ws.onopen = (e) => {
      console.log("open");
    };
  });

  onDestroy(() => {
    ws.onclose = () => {
      console.log("close");
    };
  });

  ws.onmessage = (e) => {
    const receivedata = JSON.parse(e.data);
    messagebox = [...messagebox, receivedata];
  };

  const Sendmessage = () => {
    if (message.length > 0) {
      const meobject = JSON.stringify({ channel: channel, username: username, message: message });
      ws.send(meobject);
      messagebox = [...messagebox, JSON.parse(meobject)];
      message = "";
    }
  };
</script>

<div class="container">
  <div class="text-center">
    <div>
      <h1>Channel : {channel}</h1>
    </div>
    <div>
      <form type="submit" on:submit|preventDefault={() => Sendmessage()}>
        <input class="form-text" type="text" bind:value={message} />
        <button type="submit" class="btn btn-dark">button</button>
        <!-- <input class="btn btn-dark" type="button" value="button" /> -->
      </form>
    </div>

    <div>
      {#each messagebox as me}
        {#if me.username !== username}
          <div class="text-start">{me.username + " : "}{me.message}</div>
          <div class="receiverdate mb-2">{moment(new Date()).format("YYYY-MM-DD HH:mm:ss")}</div>
        {:else if me.username === username}
          <div class="text-end">{me.username + " : "}{me.message}</div>
          <div class="senderdate mb-2">{moment(new Date()).format("YYYY-MM-DD HH:mm:ss")}</div>
        {/if}
      {/each}
    </div>
  </div>
</div>

<style>
  .receiverdate {
    text-align: start;
    font-size: 0.3vh;
  }
  .senderdate {
    text-align: end;
    font-size: 0.3vh;
  }
</style>
