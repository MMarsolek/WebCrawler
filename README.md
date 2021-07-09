# WebCrawler
This is a webcrawler that gathers YouTube data. The user can either used a built in URL or use the URL of the video that they would like to start at. The crawler uses multiple-threads to scower the internet for data. It one thread gathers data from the starting video while the other one access the video from the Suggested Videos list attached to the current video. Once a site has been accessed, the URL will be added to a stack so that if the program crashes, it can pick up from the last visited video. This also allows us to ensure we have set stop point for the crawler.
## Data
This webcrawler is designed to gather YouTube video data including the number of views, number of ratings, number of comments, the title, and the date it was uploaded. This information will be stored in a CSV that can then be manipulated and worked on. Once a site has been accessed, the URL will be added to a stack so that if the program crashes, it can pick up from the last visited video. This also allows us to ensure we have set stop point for the crawler.
## Language and Libraries
This webcrawler was created using the Java language and the JSoup API and library. JSoup is under the MIT Licence and can be found [here.](https://jsoup.org/)
## Usage
