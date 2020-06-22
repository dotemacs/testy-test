(ns crypto.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
 :account
 (fn [db _]
   (:account db)))

(reg-sub
 :approval
 (fn [db _]
   (:approval db)))

(reg-sub
 :deposit
 (fn [db _]
   (:deposit db)))

(reg-sub
 :recipient-wallet
 (fn [db _]
   (:recipient-wallet db)))

(reg-sub
 :start-time
 (fn [db _]
   (:start-time db)))

(reg-sub
 :end-time
 (fn [db _]
   (:end-time db)))
