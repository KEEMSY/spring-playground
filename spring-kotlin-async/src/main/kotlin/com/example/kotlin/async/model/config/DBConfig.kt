//package com.example.kotlin.async.model.config
//
//import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
//import io.r2dbc.postgresql.PostgresqlConnectionFactory
//import io.r2dbc.spi.ConnectionFactory
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
//import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
//
//@Configuration
//@EnableR2dbcRepositories
//class DBConfig : AbstractR2dbcConfiguration() {
//
//    @Bean
//    override fun connectionFactory(): ConnectionFactory {
//        return PostgresqlConnectionFactory(
//            PostgresqlConnectionConfiguration.builder()
//                .host("localhost")
//                .port(15432)
//                .database("local_postgres_db")
//                .username("dev")
//                .password("dev")
//                .build()
//        )
//    }
//
////    // 테스트 데이터를 넣고 싶을 때, 진행 할 것
////    @Bean
////    fun initializer(connectionFactory: ConnectionFactory): ConnectionFactoryInitializer {
////        val initializer = ConnectionFactoryInitializer()
////        initializer.setConnectionFactory(connectionFactory)
////        initializer.setDatabasePopulator(
////            ResourceDatabasePopulator(
////                ClassPathResource("schema.sql"),
////                ClassPathResource("data.sql")
////            )
////        )
////        return initializer
////    }
//}