<?php

use Monolog\Handler\StreamHandler;
use Monolog\Processor\PsrLogMessageProcessor;

return [
    'default' => 'stack',

    'channels' => [
        'stack' => [
            'driver' => 'stack',
            'channels' => [
                'console',
                env('APP_ENV') === 'production' ? 'daily' : 'single',
            ],
            'ignore_exceptions' => false,
        ],

        'console' => [
            'driver' => 'monolog',
            'handler' => StreamHandler::class,
            'handler_with' => [
                'stream' => 'php://stderr',
            ],
            'level' => env('LOG_LEVEL', 'debug'),
            'processors' => [PsrLogMessageProcessor::class],
        ],

        'single' => [
            'driver' => 'single',
            'path' => storage_path('logs/laravel.log'),
            'level' => env('LOG_LEVEL', 'debug'),
            'replace_placeholders' => true,
        ],

        'daily' => [
            'driver' => 'daily',
            'path' => storage_path('logs/laravel.log'),
            'level' => env('LOG_LEVEL', 'debug'),
            'days' => env('LOG_DAILY_DAYS', 14),
            'replace_placeholders' => true,
        ],

        'emergency' => [
            'path' => storage_path('logs/laravel.log'),
        ],
    ],
];
