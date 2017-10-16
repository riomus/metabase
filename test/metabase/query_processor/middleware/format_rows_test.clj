(ns metabase.query-processor.middleware.format-rows-test
  (:require [metabase.query-processor.middleware.format-rows :refer :all]
            [expectations :refer :all]
            [metabase.test
             [data :as data]
             [util :as tu]]
            [metabase.test.data.dataset-definitions :as defs]
            [metabase.driver.generic-sql :as sql]
            [metabase.query-processor.middleware.expand :as ql]
            [metabase.query-processor-test :as qpt]
            [metabase.test.data.datasets :refer [*engine*]]))

(qpt/expect-with-non-timeseries-dbs-except #{:oracle}
  [[1 "Plato Yeshua" "2014-04-01T00:00:00.000Z" "08:30:00.000Z"]
   [2 "Felipinho Asklepios" "2014-12-05T00:00:00.000Z" "15:15:00.000Z"]
   [3 "Kaneonuskatew Eiran" "2014-11-06T00:00:00.000Z" "16:15:00.000Z"]
   [4 "Simcha Yan" "2014-01-01T00:00:00.000Z" "08:30:00.000Z"]
   [5 "Quentin Sören" "2014-10-03T00:00:00.000Z" "17:30:00.000Z"]]
  (->> (data/with-db (data/get-or-create-database! defs/test-data-with-time)
         (data/run-query users
           (ql/limit 5)))
       qpt/rows))

(qpt/expect-with-non-timeseries-dbs-except #{:oracle}
  (if (qpt/supports-report-timezone? *engine*)
    [[1 "Plato Yeshua" "2014-04-01T00:00:00.000Z" "08:30:00.000-08:00"]
     [2 "Felipinho Asklepios" "2014-12-05T00:00:00.000Z" "15:15:00.000-08:00"]
     [3 "Kaneonuskatew Eiran" "2014-11-06T00:00:00.000Z" "16:15:00.000-08:00"]
     [4 "Simcha Yan" "2014-01-01T00:00:00.000Z" "08:30:00.000-08:00"]
     [5 "Quentin Sören" "2014-10-03T00:00:00.000Z" "17:30:00.000-08:00"]]

    [[1 "Plato Yeshua" "2014-04-01T00:00:00.000Z" "08:30:00.000Z"]
     [2 "Felipinho Asklepios" "2014-12-05T00:00:00.000Z" "15:15:00.000Z"]
     [3 "Kaneonuskatew Eiran" "2014-11-06T00:00:00.000Z" "16:15:00.000Z"]
     [4 "Simcha Yan" "2014-01-01T00:00:00.000Z" "08:30:00.000Z"]
     [5 "Quentin Sören" "2014-10-03T00:00:00.000Z" "17:30:00.000Z"]])
  (tu/with-temporary-setting-values [report-timezone "America/Los_Angeles"]
    (->> (data/with-db (data/get-or-create-database! defs/test-data-with-time)
           (data/run-query users
             (ql/limit 5)))
         qpt/rows)))
