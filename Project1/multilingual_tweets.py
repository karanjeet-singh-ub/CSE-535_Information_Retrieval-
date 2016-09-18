from twython import Twython
try:
    import simplejson
except ImportError:
    import json as simplejson
import json
import codecs
fh1=codecs.open("tweets.json","a+", "utf-8")
APP_KEY = 'xxxxxxxxxx'
APP_SECRET = 'xxxxxx'
twitter = Twython(APP_KEY, APP_SECRET, oauth_version=2)
ACCESS_TOKEN = twitter.obtain_access_token()
twitter = Twython(APP_KEY, access_token=ACCESS_TOKEN)
res=twitter.search(q='politics', lang="ru", count=100)
for each in res['statuses']:
	data = {}
	data["u_id"] = each["id"]
	data["text"] = each["text"]
	data["text"].encode("UTF-8")
	data["lang"] = each["lang"]
	data["created_at"] = each["created_at"]
	data["source"] = each["source"]
	data["geo"] = each["geo"]
	data["retweet_count"] = each["retweet_count"]
	data["coordinates"] = each["coordinates"]
	data["in_reply_to_screen_name"] = each["in_reply_to_screen_name"]
	data["in_reply_to_user_id_str"] = each["in_reply_to_user_id_str"]
	data["in_reply_to_status_id_str"] = each["in_reply_to_status_id_str"]
	data["id_str"] = each["id_str"]
	data["user"] = {}
	data["user"]["id"] = each["user"]["id"]
	data["user"]["followers_count"] = each["user"]["followers_count"]
	data["user"]["friends_count"] = each["user"]["friends_count"]
	data["user"]["location"] = each["user"]["location"]
	data["user"]["screen_name"] = each["user"]["screen_name"]
	data["user"]["url"] = each["user"]["url"]
	data["user"]["id"] = each["user"]["id"]
	data["user"]["profile_background_image_url_https"] = each["user"]["profile_background_image_url_https"]
	data["user"]["created_at"] = each["user"]["created_at"]
	data["user"]["profile_image_url_https"] = each["user"]["profile_image_url_https"]
	data["entities"]={}
	data["entities"]["hashtags"] = each["entities"]["hashtags"]
	print json.dump(data,fh1,ensure_ascii=False)
	fh1.write(",\n")
fh1.close()
