FROM python
COPY . ./covis
WORKDIR ./covis
RUN pip install -r requirements.txt
RUN apt-get update && apt-get -y install cron
RUN chmod 0644 cron
RUN crontab
CMD ["cron", "-f"]
