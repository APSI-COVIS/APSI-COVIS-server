import os
import pandas
from sqlalchemy import create_engine

data_raw = pandas.read_csv('https://raw.githubusercontent.com/datasets/covid-19/master/data/time-series-19-covid-combined.csv')
engine = create_engine('postgresql://' + os.environ['POSTGRES_USER'] + ':' + os.environ['POSTGRES_PASSWORD'] + '@db:5432/' + os.environ['POSTGRES_DB'])
data_raw.to_sql('covid', engine, if_exists='append')