# Hibiscus
Hibiscus, simplified mobile blog application. [GooglePlay](https://play.google.com/store/apps/details?id=com.tonyjs.hibiscus)

This repo is using MVVM(without Databinding), Rxjava2, Requery(orm), Retrofit2, Dagger2, Glide, anko and etc.

![Image1](https://lh3.googleusercontent.com/Nwx4a4_tpQvM6oCuPdw74bBfiZDsbVS05ZciiFpZtGUrqiE6meiVhFWfa_t5Fd4Z7UI=h250-rw) 
![Image2](https://lh3.googleusercontent.com/Zy4_lgxp4MtVLlpbtoj-XXDuzTjsWcdc_YyupvmmAUmZGgnxFN60kQI-LibNEOmFWHc=h250-rw) 
![Image3](https://lh3.googleusercontent.com/kMqMboHiPuyQ7k-2DAoRBGwLIzwP4SUZxiXJ3-NgYAgCmcCJWh87oSZkeJ1E52yxfw=h250-rw) 
![Image4](https://lh3.googleusercontent.com/FO1k0rbEEf35pOFiwPn7Ca9_qUkhoi3xGOJox_5ocpPZviAqZVi2Z1zIGwWKANpcjm1K=h250-rw) 
![Image5](https://lh3.googleusercontent.com/s-sap7RYNAMObk4NREKGmtmQkt6q2IfUFufsXetYsAiLY8e2qLg3Vk08gZUeV8Beuw=h250-rw)

### Designing the app. MVVM

##### ViewModel  - communication between Ui and Data layer.
* UserViewModel : deal with User data.(nickname and token. the token will be taken into [Telegraph Api](http://telegra.ph/api))
* PostViewModel : deal with Post data.(title, created time and any texts, images)

##### Repository - Create, Read, Update, Delete the Model(representative the Application).
* LocalRepository : deal with Data which provided by Database, ContentProvider, Preferences and etc without networking.
* RemoteRepository : deal with Data which provided by Networking.

##### Dependency Injection - using Dagger 2
* AppModule : provide classes from Application(Context, Resources, ContentResolver).
* DatabaseModule : provide Database(using [Requery](https://github.com/requery/requery))
* NetworkModule : provide the class which can networking(using [Retrofit2](https://github.com/square/retrofit)).
* MapperModule : provide mapper classes. the Model has the source from.
  * ~Entity : from the Local(Database, ContentProvider, Preferences). eg. PostEntity
  * ~DTO : from the Remote. eg. UserDTO
* RepositoryModule : provide Repository classes. eg. PostLocalRepository
* DataComponent : wrap all classes from Modules. and used for injecting.

##### Application structure
* MainActivity : the one and only activity for this application.
* PostListFragment : show the posts.
* CreatePostFragment : show users to create posts.
* PhotoListFragment : show users photo list from ContentProvider.

##### Flow
if not the application contains any data, shows the guide(like ChatBot).
When the User input the nickname and confirm, try to retrieve token from `Telegraph` using nickname. 
and then guide to create post. User can create the post and shows the posts.

##### Todo features
* update the post.
* export the post into `Telegraph` and user can share the link.
* filter by date, tags and etc.
* zoom the image.


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
