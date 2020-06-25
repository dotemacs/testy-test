(ns crypto.main
  (:require [reagent.dom :as rdom]
            [re-frame.core :as rf]
            [crypto.events]
            [crypto.subs]
            ["ethers" :as ethers]
            [goog.string :as gstring]
            [goog.string.format]
            [crypto.erc20]
            [crypto.sablier]
            [cljs.core.async :refer [go]]
            [cljs.core.async.interop :refer-macros [<p!]]
            ["bignumber.js" :refer [BigNumber]]))


(defn make-contract
  [{:keys [address abi signer]}]
  (new (.-Contract ethers) address abi signer))


(defn amount-to-stream
  [{:keys [start-time end-time amount]}]
  (let [calc-deposit (* amount (Math/pow 10 18))
        remain (mod calc-deposit (- end-time start-time))
        amount-to-stream (str (-> calc-deposit BigNumber (.minus remain)))]
    {:start-time (int start-time)
     :end-time (int end-time)
     :calc-deposit calc-deposit
     :remainder remain
     :amount-to-stream amount-to-stream}))


(defn enable-metamask
  []
  (js-invoke js/window.ethereum "enable"))


(defn populate-acount-info
  "When account information is updated from MetaMask, grab account
  info."
  [provider]
  (.on js/window.ethereum "accountsChanged"
       #(rf/dispatch [:add-account (first %)])))


(defn transact
  [provider]
  (go
    (let [signer (.getSigner provider 0)
          recipient-wallet (rf/subscribe [:recipient-wallet])
          deposit (rf/subscribe [:deposit])
          start-time (rf/subscribe [:start-time])
          end-time (rf/subscribe [:end-time])
          payload (amount-to-stream {:start-time @start-time
                                     :end-time @end-time
                                     :amount @deposit})
          sablier-testnet-address "0xc04Ad234E01327b24a831e3718DBFcbE245904CC"
          sablier2 (make-contract {:address sablier-testnet-address
                                   :abi crypto.sablier/data
                                   :signer signer})
          testnet-dai-address "0xc3dbf84abb494ce5199d5d4d815b10ec29529ff8"
          token2 (make-contract {:address testnet-dai-address
                                 :abi crypto.erc20/data
                                 :signer signer})]

      (js/alert "Asking for the streaming approval...")
      (-> (js-invoke token2 "approve"
                     (.-address sablier2)
                     (:amount-to-stream payload))
          (<p!)
          (.wait)
          (<p!))

      (js/alert "Approval for streaming was granted, creating stream...")

      (-> (js-invoke sablier2 "createStream"
                     (str @recipient-wallet)
                     (:amount-to-stream payload)
                     (.-address token2)
                     (:start-time payload)
                     (:end-time payload))
          (<p!)
          (.wait)
          (<p!))
      (js/alert (gstring/format "Streaming of %d to %s, from %s to %s set up!"
                                @deposit
                                @recipient-wallet
                                (:start-time payload)
                                (:end-time payload))))))


(defn ui
  []
  (let [provider (new (.-Web3Provider (.-providers ethers)) (.-ethereum js/window))
        _ (populate-acount-info provider)
        _ (-> (.listAccounts provider)
              (.then #(rf/dispatch [:add-account (first %)])))
        account (rf/subscribe [:account])]
    [:div
     (if (nil? @account)
       [:div
        [:button {:on-click #(enable-metamask)}
         "Connect MetaMask"]]
       (str "Account is: " @account))
     [:div
      [:div
       [:p "Deposit"]
       [:input {:id "desposit"
                :placeholder "deposit"
                :on-change #(rf/dispatch [:deposit (-> % .-target .-value)])}]]

      [:div
       [:p "Recipient wallet"]
       [:input {:id "recipient"
                :placeholder "recipient"
                :on-change #(rf/dispatch [:recipient-wallet (-> % .-target .-value str)])}]]
      [:div
       [:p "Start time"]
       [:div "(Please enter is as epoch. e.g. 1595933400 for  July 28, 2020 10:50:00 AM GMT) "]
       [:input {:id "start-time"
                :placeholder "start time"
                :on-change #(rf/dispatch [:start-time (-> % .-target .-value)])}]]
      [:div
       [:p "End time"]
       [:div "(Please enter is as epoch. e.g. 1598611800 for August 28, 2020 10:50:00 AM GMT) "]
       [:input {:id "end-time"
                :placeholder "end time"
                :on-change #(rf/dispatch [:end-time (-> % .-target .-value)])}]]
      [:button {:on-click #(transact provider)}
       "Submit"]]]))


(defn ^:dev/after-load start []
  (rf/dispatch-sync [:initialize-db])
  (js/console.log "start")
  (rdom/render [ui] (js/document.getElementById "app")))


(defn ^:export run []
  (js/console.log "run")
  (start))
