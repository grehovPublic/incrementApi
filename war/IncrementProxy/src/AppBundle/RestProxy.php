<?php
namespace AppBundle;
use Symfony\Component\HttpFoundation\Request;

class RestProxy {
    const GET = "GET";
    const POST = "POST";
    const DELETE = "DELETE";
    const PUT = "PUT";
    const PATCH = "PATCH";
    const OPTIONS = "OPTIONS";
    
    private $request;
    private $curl;
    private $content;
    private $headers;
    private $status;
    private $actions = [
        self::GET     => 'doPatch',
        self::POST    => 'doPost',
        self::DELETE  => 'doDelete',
        self::PUT     => 'doPatch',
        self::PATCH   => 'doPatch',
        self::OPTIONS => 'doOptions',
    ];
    public function __construct(Request $request, CurlWrapper $curl) {
        $this->request = $request;
        $this->curl    = $curl;
    }
    
    public function getHeaders() {
        return $this->headers;
    }
    
    public function getContent() {
        return $this->content;
    }
    
    public function getStatus() {
        return $this->status;
    }
    
    public function dispatch($url) {
        $queryString   = $this->request->getQueryString();
        $action        = $this->getActionName($this->request->getMethod());
        $this->content = $this->curl->$action($url, $queryString);
        $this->headers = $this->curl->getHeaders();
    }
    
    private function getActionName($requestMethod) {
        if (!array_key_exists($requestMethod, $this->actions)) throw \Exception("Method not allowed");
        return $this->actions[$requestMethod];
    }
}