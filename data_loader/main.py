import pandas
import os
import io
import requests
from sqlalchemy import create_engine

response = requests.get('https://datahub.io/JohnSnowLabs/population-figures-by-country/r/population-figures-by-country-csv.csv')
file = io.StringIO(response.content.decode('utf-8'))
pop_data = pandas.read_csv(file)
population = pop_data[['Country','Country_Code',pop_data.columns[-1]]]
population.columns = ['country', 'population']

data_raw = pandas.read_csv('https://raw.githubusercontent.com/datasets/covid-19/master/data/time-series-19-covid-combined.csv')
data_raw['Date'] = pandas.to_datetime(data_raw['Date'])
engine = create_engine('postgresql://' + os.environ['POSTGRES_USER'] + ':' + os.environ['POSTGRES_PASSWORD'] + '@db:5432/' + os.environ['POSTGRES_DB'])
data_raw.to_sql('covid', engine, if_exists='append')
population.to_sql('population', engine, if_exists='append')