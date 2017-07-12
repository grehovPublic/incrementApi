<?php

namespace AppBundle\Controller;

require_once __DIR__ . '/../../../vendor/autoload.php';
use Symfony\Component\HttpFoundation\Request;
use AppBundle\RestProxy;
use AppBundle\CurlWrapper;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Route;
use Psr\Log\LoggerInterface;

class ProxyIndex extends \Symfony\Bundle\FrameworkBundle\Controller\Controller
{
    const REST_USERNAME = "demo";
    const REST_PASSWORD = "demo";
    const REST_URL = "localhost:8090/api/increment";
    
    private $logger;
    
     public function __construct( LoggerInterface $logger ) {
        $this->logger = $logger;     
     }
    
    /**
     * @Route("/proxy")
     */
    public function numberAction()
    {
        $requestHeaders = [
            'Content-Type:application/json',
            'Authorization: Basic ' . base64_encode(self::REST_USERNAME 
                    . ":" . self::REST_PASSWORD)
        ];
        $curlOptions = [
            CURLOPT_SSL_VERIFYPEER => 0,
            CURLOPT_SSL_VERIFYHOST => 0
        ];
        
        $request = Request::createFromGlobals();

        $proxy = new RestProxy( $request,
            new CurlWrapper($requestHeaders, $curlOptions)
        );

        $this->logger->info("Value to increment:" . $request->getContent());
        $proxy->dispatch(self::REST_URL);
        $this->logger->info("Incremented value:" . $request->getContent());
        return $this->json(array('incremented' => $proxy->getContent()));
    }
}