(ns crypto.events
  (:require
   [re-frame.core :refer [reg-event-db]]
   [crypto.db :as db :refer [app-db]]))


(reg-event-db
 :initialize-db
 (fn [_ _]
   app-db))

(reg-event-db
 :add-account
 (fn [db [_ value]]
   (assoc db :account value)))

(reg-event-db
 :approval
 (fn [db [_ value]]
   (assoc db :approval value)))

(reg-event-db
 :deposit
 (fn [db [_ value]]
   (assoc db :deposit value)))

(reg-event-db
 :recipient-wallet
 (fn [db [_ value]]
   (assoc db :recipient-wallet value)))

(reg-event-db
 :start-time
 (fn [db [_ value]]
   (assoc db :start-time value)))

(reg-event-db
 :end-time
 (fn [db [_ value]]
   (assoc db :end-time value)))
