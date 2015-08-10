{:server
 {:port 3743
  :wamp
  {:realm "default"
   ; :router-uri "ws://10.111.248.48:3745/ws" ; prod
   :router-uri "ws://10.111.244.18:3745/ws" ; dev
   :debug? true}
  :client
  {:wamp
   {; :router-uri "ws://api.etheride.com:3745/ws" ; prod
    :router-uri "ws://104.197.11.59:3745/ws" ; dev
    }}}
 :api
 {}}
