import simplejson as json
import csv
import pprint

#####################################################
# This script takes the hand-coded csvs and converts them to json 
# so that we have good sample data files. I needed to clean up the CSVs
# so I'm leaving them in here for reference or in case we need to 
# re-generate the json.
#####################################################

def addArticle(real_country, article_text, mediacloudId):
	article = dict()
	article['handCodedPlaceName'] = real_country
	article['handCodedCountryCode'] = real_country
	article['text'] = article_text
	article['mediacloudId'] = mediacloudId
	result.append(article)


#---------- MAIN ---------------
sentences = csv.DictReader(open('csv/huffpo.csv', 'rU'), delimiter=',', quotechar='"')
result = list()

article_text = ''
real_country = ''
mediacloudId = ''

for row in sentences:
	
	if (  row['sentence_number'] != '' and int(row['sentence_number']) == -1 and real_country is not ''):		
		addArticle(real_country,article_text,mediacloudId)
		article_text = ""

	if(len(row['real_country']) > 0):
		real_country = row['real_country']
		mediacloudId = row['stories_id']
	

	sentence = row['sentence'].encode('ascii', 'ignore') + ' '
	article_text+=sentence

	#add the last article
	if (row['sentence_number'] == ''):

		addArticle(real_country,article_text,mediacloudId)
		break
    

with open('huffpo.json', 'w') as outfile:
  json.dump(result, outfile)
