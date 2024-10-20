package com.example.e_commorce_fashions.app.utils

import android.app.Application
import android.content.Context
import androidx.datastore.core.Closeable
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavBackStackEntry
import androidx.room.Room
import com.example.e_commorce_fashions.app.AppState
import com.example.e_commorce_fashions.data.datasrouce.DataSource
import com.example.e_commorce_fashions.data.InternetConnectivityChecker
import com.example.e_commorce_fashions.data.datasrouce.local.LocalServices
import com.example.e_commorce_fashions.data.datasrouce.local.NotesDao
//import com.example.e_commorce_fashions.data.datasrouce.local.NotesRoomDatabase
import com.example.e_commorce_fashions.data.datasrouce.remote.ApiServices
import com.example.e_commorce_fashions.data.datasrouce.remote.PaymentApiServices
import com.example.e_commorce_fashions.data.datasrouce.remote.PaypalPaymentApiServices
import com.example.e_commorce_fashions.data.datasrouce.remote.RetrofitBuilder
import com.example.e_commorce_fashions.data.repository.RepositoryImpl
import com.example.e_commorce_fashions.domain.repository.Repository
import com.example.e_commorce_fashions.presentation.common.GoogleAuth.GoogleAuthUiClient
import com.example.e_commorce_fashions.presentation.resources.config.navigator.SharedData
import com.example.e_commorce_fashions.presentation.views.auth.login.view_state.LoginViewState
import com.example.e_commorce_fashions.presentation.views.auth.sign_up.view_state.SignUpViewState
import com.example.e_commorce_fashions.presentation.views.home.view_state.HomeViewState
import com.example.e_commorce_fashions.presentation.views.view_all.view_state.ViewAllProductsStateView
import com.google.android.gms.auth.api.identity.SignInClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.google.android.gms.auth.api.identity.Identity

val Context.appDataStore by preferencesDataStore(Constants.APP_DATA_STORE)


//@Module
//@InstallIn(SingletonComponent::class)
//object Di {
//
//    val appState: AppState by lazy { AppState() }
//
//    @Provides
//    @Singleton
//    fun apiServices(): ApiServices = ApiServices()
//
//    @Provides
//    @Singleton
//    fun localServices(): LocalServices = LocalServices()
//
//    @Provides
//    @Singleton
//    fun internetConnectivityChecker(): InternetConnectivityChecker = InternetConnectivityChecker()
//
//    @Provides
//    @Singleton
//    fun preferences(context: Application): DataStorePreferences =
//        DataStorePreferences(context.appDataStore)
//
//    @Provides
//    @Singleton
//    fun dataSource(
//        apiServices: ApiServices,
//        localServices: LocalServices,
//        internetConnectivityChecker: InternetConnectivityChecker
//    ): DataSource = DataSource(
//        apiServices,
//        localServices,
//        internetConnectivityChecker
//    )
//
//    @Provides
//    @Singleton
//    fun repository(
//        dataSource: DataSource,
//        internetConnectivityChecker: InternetConnectivityChecker
//    ): Repository = RepositoryImpl(dataSource, internetConnectivityChecker)
//
//    @Provides
//    @Singleton
//    fun appLanguages(): AppLanguages = AppLanguages()
//
//}

//
//abstract class DiAbstract {
//    @Volatile
//    protected var _appState: AppState? = null
//    abstract val appState: AppState
//
//    @Volatile
//    protected var _apiServices: ApiServices? = null
//    abstract val apiServices: ApiServices
//
//    @Volatile
//    protected var _localServces: LocalServices? = null
//    abstract val localServices: LocalServices
//
//    @Volatile
//    protected var _repository: Repository? = null
//    abstract val repository: Repository
//
//    @Volatile
//    protected var _internetConnectivityChecker: InternetConnectivityChecker? = null
//    abstract val internetConnectivityChecker: InternetConnectivityChecker
//
//    @Volatile
//    protected var _dataSource: DataSource? = null
//    abstract val dataSource: DataSource
//
////    @Volatile
////    protected var _database: NotesRoomDatabase? = null
////    abstract val database: (context: Context) -> NotesRoomDatabase
//
////    @Volatile
////    protected var _notesDao: NotesDao? = null
////    abstract val notesDao: NotesDao
//
//    @Volatile
//    protected var _appLanguages: AppLanguages? = null
//    abstract val appLanguages: AppLanguages
//
//    @Volatile
//    protected var _preferences: DataStorePreferences? = null
//    abstract val preferences: DataStorePreferences
//
////    @Volatile
////    protected var _googleAuthUiClient: GoogleAuthUiClient? = null
////    abstract val googleAuthUiClient: GoogleAuthUiClient
//
////    fun clearGoogleAuthUiClient() {
////        _googleAuthUiClient = null
////    }
//
//    fun clearDataStorePreferences() {
//        _preferences = null
//    }
//
//    fun clearAppState() {
//        _appState = null
//    }
//
//    fun clearApiServices() {
//        _apiServices = null
//    }
//
//    fun clearLocalServices(){
//        _localServces = null
//    }
//
//    fun clearRepository() {
//        _repository = null
//    }
//
//    fun clearInternetConnectivityChecker() {
//        _internetConnectivityChecker = null
//    }
//
//    fun clearDataSource() {
//        _dataSource = null
//    }
//
////    fun closeDatabase() {
////        _database?.close()
////        _database = null
////    }
//
//    fun clearAll() {
//        clearAppState()
//        clearApiServices()
//        clearLocalServices()
//        clearRepository()
//        clearInternetConnectivityChecker()
//        clearDataSource()
////        closeDatabase()
//        clearDataStorePreferences()
////        clearGoogleAuthUiClient()
//    }
//}
//
//
//object Di : DiAbstract() {
//    override val appState: AppState
//        get() = _appState ?: synchronized(this) {
//            _appState ?: AppState().also { _appState = it }
//        }
//
//    override val apiServices: ApiServices
//        get() = _apiServices ?: synchronized(this) {
//            // this when i use teh retrofit
//            // _apiServices ?: RetrofitBuilder().get().also { _apiServices = it }
//            _apiServices ?: ApiServices().also { _apiServices = it }
//        }
//
//    override val localServices: LocalServices
//        get() = _localServces ?: synchronized(this) {
//            _localServces ?: LocalServices().also { _localServces = it }
//        }
//
//
//    override val repository: Repository
//        get() = _repository ?: synchronized(this) {
//            _repository ?: RepositoryImpl(
//                dataSource,
//                internetConnectivityChecker
//            ).also { _repository = it }
//        }
//
//    override val internetConnectivityChecker: InternetConnectivityChecker
//        get() = _internetConnectivityChecker ?: synchronized(this) {
//            _internetConnectivityChecker
//                ?: InternetConnectivityChecker().also { _internetConnectivityChecker = it }
//        }
//
//    override val dataSource: DataSource
//        get() = _dataSource ?: synchronized(this) {
//            _dataSource ?: DataSource(apiServices, localServices,internetConnectivityChecker).also {
//                _dataSource = it
//            }
//        }
//
////    override val database: (context: Context) -> NotesRoomDatabase = { context ->
////        _database ?: synchronized(this) {
////            _database ?: Room.databaseBuilder(
////                context,
////                NotesRoomDatabase::class.java,
////                Constants.DATABASE_NAME
////            ).build().also { _database = it }
////        }
////    }
////    override val notesDao: NotesDao
////        get() = _notesDao ?: synchronized(this) {
////            if (_database == null) throw Exception("Database not initialized")
////            _database!!.notesDao.also { _notesDao = it }
////        }
//
//    override val appLanguages: AppLanguages
//        get() = _appLanguages ?: synchronized(this) {
//            _appLanguages ?: AppLanguages().also { _appLanguages = it }
//        }
//
//    override val preferences: DataStorePreferences
//        get() = _preferences ?: synchronized(this) {
//            if(_preferences == null) throw Exception("Preferences not initialized")
//            _preferences!!
//        }
////
////    override val googleAuthUiClient: GoogleAuthUiClient
////        get()  = _googleAuthUiClient ?: synchronized(this){
////            if(_googleAuthUiClient == null) throw Exception("Google Auth Ui Client not initialized")
////            _googleAuthUiClient!!
////        }
//
//    fun providePreferences(context: Context) {
//        _preferences = DataStorePreferences(context.appDataStore)
//    }
//
////    fun provideGoogleAuthUiClient(context: Context, onClientTap: SignInClient) {
////        _googleAuthUiClient = GoogleAuthUiClient(context, onClientTap)
////    }
//
//}

object Di {

    val appState: AppState by Lazy.normal { AppState() }
    val apiServices: ApiServices by Lazy.normal { ApiServices() }
    val retrofitBuilder: RetrofitBuilder by Lazy.normal { RetrofitBuilder() }
    val localServices: LocalServices by Lazy.normal { LocalServices() }
    val repository: Repository by Lazy.normal { RepositoryImpl(dataSource, internetConnectivityChecker) }
    val internetConnectivityChecker: InternetConnectivityChecker by Lazy.normal { InternetConnectivityChecker() }
    val paymentApiServices: Lazy<PaymentApiServices> = Lazy.provider {
        retrofitBuilder.getPaymentApiServices(it)
    }
    val paypalPaymentApiServices: Lazy<PaypalPaymentApiServices> = Lazy.provider {
        retrofitBuilder.getPaypalPaymentApiServices(it)
    }
    val dataSource: DataSource by Lazy.normal {
        DataSource(
            apiServices,
            localServices,
            internetConnectivityChecker,
            paymentApiServices.value
        )
    }
    val appLanguages: AppLanguages by Lazy.normal { AppLanguages() }
    val preferences: Lazy<DataStorePreferences> = Lazy.provider {
            DataStorePreferences(it.appDataStore)
    }
    val googleAuthUiClient: Lazy<GoogleAuthUiClient> = Lazy.provider {
        GoogleAuthUiClient(it, Identity.getSignInClient(it))
    }

    val sharedData: Lazy<SharedData> = Lazy.normal {
        SharedData()
    }
}



class Lazy<T> private constructor(
    private val initializer: (Application?) -> T
) : LifecycleEventObserver {

    @Volatile
    private var _value: T? = null
    private var _context: Application? = null
    private var _lifecycle: Lifecycle? = null
    private var _onDispose: ((T) -> Unit)? = null

    companion object {
        // Factory methods for different use cases
        fun <T> normal(initializer: () -> T): Lazy<T> {
            return Lazy<T> { _ -> initializer() }
        }

        fun <T> provider(initializer: (Application) -> T): Lazy<T> {
            return Lazy<T> { con ->
                if (con != null) initializer(con) else throw IllegalStateException(
                    "Context not initialized"
                )
            }
        }
    }

    operator fun getValue(thisRef: Any?, property: kotlin.reflect.KProperty<*>): T {
        return _value ?: synchronized(this) {
            _value ?: _context?.let { context ->
                initializer(context).also { _value = it }
            } ?: initializer(null).also { _value = it }
        }
    }
    val value: T get() = getValue(this, ::value)


    fun provide(context: Application): Lazy<T> {
        _context = context
        return this
    }

    fun owner(cycle: Lifecycle): Lazy<T> {
        if (_lifecycle != cycle) {
            _lifecycle?.removeObserver(this)
            _lifecycle = cycle
            if (cycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                _lifecycle!!.addObserver(this)
            }
        }
        return this
    }

    fun clear() {
        synchronized(this) {
            _value?.let {
                if (it is Closeable) { // Assuming the object implements Closeable
                    it.close()
                }
            }
                _value = null
            _context = null
            _lifecycle = null
        }
    }

    fun onDispose(callback: (T) -> Unit) : Lazy<T>{
        _onDispose = callback
        return this
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
       if(event == Lifecycle.Event.ON_DESTROY){
           _lifecycle!!.removeObserver(this)
           clear()
           _onDispose?.invoke(value)
       }
    }

}