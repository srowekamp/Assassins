//
//  WebRequest.swift
//  Assassins
//
//  Created by Scott Rowekamp on 10/5/16.
//  Copyright © 2016 LA-05. All rights reserved.
//
typealias DownloadComplete = () -> ()

import Foundation
import Alamofire

class WebRequest {
 
    private var _username:String!
    private var _password:String!
    
    var username:String {
        if _username == nil {
            _username = ""
        }
        return self._username!
    }
    
    var password:String {
        if _password == nil {
            _password = ""
        }
        return self._password!
    }
    
    func getUser(completed: DownloadComplete) {
        // Alamofire
        let url = URL(string: "http://proj-309-la-05.cs.iastate.edu/connect.php")!
        Alamofire.request(url, method: HTTPMethod.get).responseJSON { (response) in
            if let data = response.result.value as? Array<Dictionary<String, AnyObject>> {
                if let name = data[0]["username"] as? String {
                    self._username = name
                }
                if let password = data[0]["password"] as? String {
                    self._password = password
                }
            }
            print("\(self._username!) , \(self._password!)")
        }
        completed()
    }
}
