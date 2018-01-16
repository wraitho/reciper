package com.peeeq.reciper.network;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * RxErrorHandlingCallAdapterFactory is intercepting responses to be able to wrap throwables into
 * {@link RetrofitException} similar to Retrofit 1.9 version's RetrofitError.
 * See <a href="http://static.javadoc.io/com.squareup.retrofit/retrofit/1.9.0/retrofit/RetrofitError.html">RetrofitError</a>
 */
public class RxErrorHandlingCallAdapterFactory extends CallAdapter.Factory {
    private final RxJava2CallAdapterFactory mOriginal;

    private RxErrorHandlingCallAdapterFactory() {
        mOriginal = RxJava2CallAdapterFactory.create();
    }

    private RxErrorHandlingCallAdapterFactory(Scheduler scheduler) {
        mOriginal = RxJava2CallAdapterFactory.createWithScheduler(scheduler);
    }

    public static CallAdapter.Factory create() {
        return new RxErrorHandlingCallAdapterFactory();
    }

    public static CallAdapter.Factory createWithScheduler(Scheduler scheduler) {
        return new RxErrorHandlingCallAdapterFactory(scheduler);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CallAdapter<?, ?> get(@NonNull Type returnType, @NonNull Annotation[] annotations, @NonNull Retrofit retrofit) {
        return new RxCallAdapterWrapper(retrofit, mOriginal.get(returnType, annotations, retrofit));
    }

    private static class RxCallAdapterWrapper<R> implements CallAdapter<R, Object> {
        private final Retrofit mRetrofit;
        private final CallAdapter<R, Object> mWrapped;

        public RxCallAdapterWrapper(Retrofit retrofit, CallAdapter<R, Object> wrapped) {
            mRetrofit = retrofit;
            mWrapped = wrapped;
        }

        @Override
        public Type responseType() {
            return mWrapped.responseType();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object adapt(@NonNull Call<R> call) {
            Object result = mWrapped.adapt(call);
            if (result instanceof Single) {
                return ((Single) result).onErrorResumeNext(new Function<Throwable, SingleSource>() {
                    @Override
                    public SingleSource apply(@NonNull Throwable throwable) throws Exception {
                        return Single.error(asRetrofitException(throwable));
                    }
                });
            }
            if (result instanceof Observable) {
                return ((Observable) result).onErrorResumeNext(new Function<Throwable, ObservableSource>() {
                    @Override
                    public ObservableSource apply(@NonNull Throwable throwable) throws Exception {
                        return Observable.error(asRetrofitException(throwable));
                    }
                });
            }

            if (result instanceof Completable) {
                return ((Completable) result).onErrorResumeNext(new Function<Throwable, CompletableSource>() {
                    @Override
                    public CompletableSource apply(@NonNull Throwable throwable) throws Exception {
                        return Completable.error(asRetrofitException(throwable));
                    }
                });
            }

            return result;
        }

        private RetrofitException asRetrofitException(Throwable throwable) {
            // We had non-200 http error
            if (throwable instanceof HttpException) {
                HttpException httpException = (HttpException) throwable;
                Response response = httpException.response();
                return RetrofitException.httpError(response.raw().request().url().toString(), response, mRetrofit);
            }
            // A network error happened
            if (throwable instanceof IOException) {
                return RetrofitException.networkError((IOException) throwable, mRetrofit);
            }
            // We don't know what happened. We need to simply convert to an unknown error
            return RetrofitException.unexpectedError(throwable, mRetrofit);
        }
    }
}
