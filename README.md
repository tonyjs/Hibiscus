# Hibiscus
Hibiscus, simplified mobile blog application. [GooglePlay](https://play.google.com/store/apps/details?id=com.tonyjs.hibiscus)

This repo is using MVVM(without Databinding), Rxjava2, Requery(orm), Retrofit2, Dagger2, Glide, anko and etc.

![Image1](https://github.com/tonyjs/Hibiscus/blob/master/sceenshots/Screenshot_1.png) 
![Image2](https://github.com/tonyjs/Hibiscus/blob/master/sceenshots/Screenshot_2.png) 
![Image3](https://github.com/tonyjs/Hibiscus/blob/master/sceenshots/Screenshot_3.png) 
![Image4](https://github.com/tonyjs/Hibiscus/blob/master/sceenshots/Screenshot_4.png) 
![Image5](https://github.com/tonyjs/Hibiscus/blob/master/sceenshots/Screenshot_5.png)

### Designing the app. MVVM

##### Repository - Create, Read, Update, Delete the Model(representative the Application).
* LocalRepository : deal with Data which provided by Database, ContentProvider, Preferences and etc without networking.
* RemoteRepository : deal with Data which provided by Networking.

##### ViewModel  - communication between Ui and Data layer.
* UserViewModel : deal with User data.(nickname and token. the token will be taken into [Telegraph Api](http://telegra.ph/api))
* PostViewModel : deal with Post data.(title, created time and any texts, images)

##### Application structure
* MainActivity : the one and only activity for this application.
* PostListFragment : show the posts.
* CreatePostFragment : show users to create posts.
* PhotoListFragment : show users photo list from ContentProvider.

##### Dependency Injection - using Dagger 2
* AppModule : provide classes from Application(Context, Resources, ContentResolver).
* DatabaseModule : provide Database(using [Requery](https://github.com/requery/requery))
* NetworkModule : provide the class which can networking(using [Retrofit2](https://github.com/square/retrofit)).
* MapperModule : provide mapper classes. Mapper classs make the Model from the source.
  * ~Entity : from the Local(Database, ContentProvider, Preferences). eg. PostEntity
  * ~DTO : from the Remote. eg. UserDTO
* RepositoryModule : provide Repository classes. eg. PostLocalRepository
* DataComponent : wrap all classes from Modules. and used for injecting.

##### Flow
if the application does not  contains any data, chat-bot-styled guide shows how to. 
When the user types the nickname and confirm, it tries to retrieve token from Telegraph using nickname, and then guides user to create a post. 
After the guide, user can create the post, and read the posts after he or she uploads the post.

##### Todo features
Edit the post.
Export the post into Telegraph so that user can share the link.
Add filter elements, like date and tags, etc.
Available to zoom image.


License
--------


    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
