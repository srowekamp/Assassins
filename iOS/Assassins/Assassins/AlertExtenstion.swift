//
//  AlertExtenstion.swift
//  Assassins
//
//  Created by Scott Rowekamp on 11/9/16.
//  Copyright © 2016 LA-05. All rights reserved.
//

import UIKit

extension UIViewController {
    
    func popUpAlert(title:String, message:String, handler:((UIAlertAction) -> Void)?){
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "Dismiss", style: .default, handler: handler))
        self.present(alert, animated: true, completion: nil)
    }
}
