<?php

namespace App\Exceptions;

use Exception;

class ServiceException extends Exception
{
    protected $code;
    protected $message;

    public function __construct($code = 500, $message = 'サービス例外')
    {
        parent::__construct($message, (int)$code);
    }
}
