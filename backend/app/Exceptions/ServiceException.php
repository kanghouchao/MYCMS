<?php

namespace App\Exceptions;

use Exception;
use Illuminate\Http\Request;

class ServiceException extends Exception
{
    protected $code;
    protected $message;

    public function __construct(string $message, int $code = 400)
    {
        parent::__construct($message, $code);
    }

    public function render(Request $request)
    {
        return response()->json([
            'message' => $this->getMessage(),
        ], $this->getCode() ?: 400);
    }
}
