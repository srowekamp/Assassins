//
//  CreateAccountVC.swift
//  Assassins
//
//  Created by Scott Rowekamp on 10/3/16.
//  Copyright © 2016 LA-05. All rights reserved.
//

import UIKit
import Alamofire
import SwiftyJSON

class CreateAccountVC: UIViewController, UIImagePickerControllerDelegate, UINavigationControllerDelegate, UITextFieldDelegate {
    
    @IBOutlet weak var navBar: UINavigationItem!
    @IBOutlet weak var userIcon: UIImageView!
    @IBOutlet weak var realName: UITextField!
    @IBOutlet weak var username: UITextField!
    @IBOutlet weak var password: UITextField!
    @IBOutlet weak var confirmPassword: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        prepareTextField(textField: realName, secure: false, capitalType: .words)
        prepareTextField(textField: username, secure: false, capitalType: .none)
        prepareTextField(textField: password, secure: true, capitalType: nil)
        prepareTextField(textField: confirmPassword, secure: true, capitalType: nil)
    }
    
    // attempts to create a user account
    // first check if the passwords match then send the information to the server and wait for a response if the account was added
    // the server could return bad account if the user already exists
    
    @IBAction func createAccount(_ sender: AnyObject) {
        // check if the passwords match
        if(password.text != confirmPassword.text) {
            popUpAlert(title: "Bad Passwords", message: "Your passwords do not match, please try again.", handler: nil)
            return
        }
        
        // attempt to create user on server
        
        // encode the image a base64 string
        let imageData = UIImageJPEGRepresentation(userIcon.image!, 10.0)
        
        var B64_ImageData:String = (imageData?.base64EncodedString())!
        
        B64_ImageData = B64_ImageData.replacingOccurrences(of: "+", with: "%2b")
        B64_ImageData = B64_ImageData.replacingOccurrences(of: "/", with: "%2f")
        B64_ImageData = B64_ImageData.replacingOccurrences(of: "=", with: "%3d")
        
        // print("\n\n\(B64_ImageData)\n\n")
        
        // set up data for server call
        
        let addUserURL = "http://proj-309-la-05.cs.iastate.edu:8080/Assassins/CreateAccount"
        
        var parameters = [String:String]()
        
        parameters["username"] = username.text!
        parameters["password"] = password.text!
        parameters["real_name"] = realName.text!
        parameters["b64_jpg"] = "%2F9j%2F4AAQSkZJRgABAQEASABIAAD%2F4QAWRXhpZgAATU0AKgAAAAgAAAAAAAD%2F2wBDAAEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH%2F2wBDAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH%2FwAARCAAxADADASIAAhEBAxEB%2F8QAHgAAAQQCAwEAAAAAAAAAAAAACQACBwgDCgEEBgX%2FxAA4EAABBAIBAgQDBAcJAAAAAAAEAQIDBQYHCAARCRITIRQxUQoyQXEVFjaBkbGzGSIkM3N0gqHB%2F8QAGgEAAwADAQAAAAAAAAAAAAAABwgJAAIGBf%2FEAC8RAAEEAgEDAwIDCQAAAAAAAAMBAgQFBgcSAAgRExQiFSEWMTI2N0FxdXaxs7T%2F2gAMAwEAAhEDEQA%2FAN%2FDprnI35qiJ%2BKqvZE7%2FL%2BK9cqvb5%2ByfX93zVfkn07L%2BfVDOXHIGxxNU1rhZkgV4cGyfI7oWbyE1QBbf8PXAPj7SQWBsXnmnKR7JQhHQqL5pi2ziDHb%2B18Y0vgttneVEIsGuQYYsKPxWZa2Mh3CJXQ2vVGqY7%2FKue74BCwpyKjBu66XEsVtMyvIlDUsasiSquKYiq0ESMPwp5R3IiqgxNVPCNRXEI5gmIr3onUjclOX%2BoeLUVMux578m0yUcyego6CkJsCD4K%2BSCMub4%2BZRaUNo8hA7ZIyrKIt3qtkhGlj7uSjH9s1pJ5KwP1TtGIRyq1x3mxSVGsb7pJ8I2%2BSVyO7J3jSRXL37d1791dca9bzS4y32pTY2ybW1ZIPe63yMl7mJK9Y54AqywsZEerRbEZpVDZRTP9H0kqLdGPMrmyRUim8ILk9HTuPjyXUZNoyFZlo4siyRk7u7Vcg8NhNiEVe4pfZitllHE9RVRDVjRJXKFkW6u4jOY2N5vo%2Bjj2mv8goot0Bo6eHLsoUsbnxrejtCzZDlJOhzAGExYAxoYLhuE1ypyc7GptYdp0WjtKjeOR2NFn1ddy6oo5F1Nh10iKRoz1lzUhr4KoOCeMYfrOsCvQUgRUK9g1RqCK%2B0N8g9u7C5WYxr8rIr8Tj6PqPXue6wxoQ8ofEcnflwZNlZ5oYELM2uuroewSSiFKNQ2SkGrXMrnCNtTHGGZ%2BzZbp3ntXjDtmg2pdZHl2D6v2JSYzqbKcmILtDmhmUEljkmFD3hr5jLCrw%2BRKEmuGJmIdTRZM2sHljrYAQg6lbDLyLWdRUceuY3F7V26afCGzGa%2Br9yY86xt8Try53unjwPPqM2I39Wii2P84lbYyip5Za%2BV6BsiChMF4YnKbBth0Ntoer1rrzTRGC181xhGF6zqIsZxCXDnGRRmxV1LH5mwWdRYFQOtZ4nufaNs2HyxtnaXK8p6031hmVZvXUtpPtsdzm0C%2BLOxa4q5kEse2HGY49c6SREjOHzYR8Fiq15kYNqjG9yNU379gWVf2f1mA4%2FrKjucYw6XVTIW1KC5pLGslVI7BfOTV8OKv1YdncicKNkKmEsaP7qUvuZQhsIzyW8fFyE11tXIMCwfWQ2b0GI3hVBd5IfkslLKfZVJLg7mOlDGprGOQcI6AkSAooiJpUgz5GRuGmhlTw%2Byrml2GmOb0xWxsLPFt1BHZQC23ZFHa0VvXHOqMixE5IF9F78WPGbVwSRK%2BBQ4xkgnMiY0sj5G%2FfCT2Ple3cnyzVOX4WJhmaZAfkU4GUkXAlpjB14ZOfbiwxV9ZZwW1ZGXPNNWOaUCS2KeOtlgZGKh8%2FezbHqHWNJhWhMXfYlVGmA7qjOuLQdoxd9l1zbS2uXXbB0c5YK82yWNa2FXOZGFHE2F0kHpSyI73VytqysNzce8w%2B2pI%2BV166zUTIw2vs%2FqjWSB1SwvD5df%2BFnTySCWrXEbIZG4PaZz2OH1RC7f4lbrQ2kZZ5OZSKcibDaUliZ7YP0nkclwOd5jQrBMjSEOKOqVg3xlk%2FB4UaRpe9A66qtfaxxuuGFiZZ2leHcX5bURpBtrYQMJk9d6eZXsCSVAxmeZ0cMUKNj%2B85XTc6NrkVFTundXKnZPdfn7e31906ibSecVmeazxW8CIikmbUi11pDE9HPEta2GIQ8aaNqeaF%2FqsSaNrkTzwTQTx%2BeGVj3y150Tv3Xt27%2FAME9vl809%2Fr3Xv2%2BqJ1VnVUfGY2t8HFh7YbcbZjFMtT7FBLHdFdBArH%2BRJxUj1VXHcvh6mc9SfPl1O%2FJn2Jciu33CmWzW0m%2B9U%2Fn1UOkh6Oa7l92o1fixv6Ua1EaiN8J1Rvn7x4D33x%2FykatoVts%2FwALrz8twJ4IUhl9LaVkDCzqCrZBHIWS%2FJQBpKptbCjkLPfWORj5xh1jBFxb0byk1TyG1JnT9E7jqwqXNKqG5Om19lA4kOPWz30mQIYRLWMghDdT2ByEzSuZHDF5pXPa5nmQ0fipcwg%2BGHDPa20arIQqXZlnUk4Hpv1PgSDnbOysWcGisq6tsIChLWTEh0OzIoIoMwGUPHp22EEojpW9anXB%2FwAV3xQ%2BRfLnjvpMvkbbXVPn20sXq8krIteasiWbDgzkucydLMJhERQoo2K1tyUWUPLFMGHDMRBLFJCx7Q1tfSeF5hsvE81La3dFlFQ%2BtI11ECvcyeWFPaaC%2BwfJ8FR7eKhV415rGXiq%2BGt6oj2tRd5z%2B3TbSUcHAperI4MhbPNn8%2FIIpQjXHyGvW44yuiyIphsEoyPZIcIKWTm%2BPm46pvyOaiov1%2Fenf81Tsv4J%2BP8A13RR08wdEWJpkm1cUDca9Bo48wrhIXOJSIKJsQ99ExndxCRDJGLYNaz1Y4IYCER0MU%2FkIx1iljZI1zHIjke1zVRURUVFT3RUVFRUX27ovt7fn0S916dxnd%2BBWeDZIjwClKKVW2cdrXS6i0i8liToyP8Aiqs5PEYS%2BGmjkKJyojkck%2FsLy6zwi%2Bi3tWrXECjgyYxFX0ZkMqt9aMXx90R3FrmPT7jKxj0ReKooMNk8lazw5%2BFmw%2BUWVSwnZXmhFfjOmNcnnExDZjlp0ZTKBSQxpoZ%2FhljiPyO7NhWOcPDqQgiGdxBYwsgii%2FtSO2H426EPilgQ%2BWuia1LGfYl4RQMmRUVZW1LMdgsHR9u7lh%2FTDXvVEasyIrndH%2F8AEZ8LPUfiNU2ChZ3sPZWuLzWcV0zCbHCzqsqgGkv0Bcct5h13XlgWrHOrQ2%2BtXl0Ns0eJB4reGFVYoQY%2FsqxTj0jI5wwOp%2F7nmnG49vhtJGd%2B0jWjy7pnEGf5VasU7iC0Y5fO8dzWq13g4Dra71RhWM6%2FxExD1GPVooiziFjIefOM90ifPOyQ9ygWRKKR7AiX0Qj4Mb%2BXT76Yyvsjvset8j7hFmS9jWF5NtCQz1mXNhQqwLBRqumrT436kWeFI8dhZD7D0jllHKnp%2Bizmo57bQ3iheM%2BdDyX2Te68xXWcM1rT61u9pZUup9O1yRk%2BlbUWqMaEAy3J7dFMCdDcZPPWXLrA4JwNrlpMlTGEAcXwRPCHzPh5ludciuR8WFn7VMCssE1IDieQCZlSUOFlTDrfZ4HfBsbX%2FpLOWjjA1DGQR3NRicZo1i4AnKLuhBtRv%2FWCabK1zrSjBUHXGvtX4ZguvoRx2i1Q1Pi1TBUSDCQQp8NAS2QVjyomd5PTkGWRZGLG9bo8Gq%2FKY9f5ARZqUzGir1kmMRkK9GOVkUqXc4DH9%2FIFMb6DVVnaCQ%2BM96N9V075AVr7fUvJO5rINNzsBt2Nx9tm5cskzSlKsisjsf76bCWI2OCrnO8CqyslPfyMBfL0I1Bb7m33mV3omdS4dLw%2FCdV3xI9fV4TjVGyDKjY%2B6c8gaxLZJjyGnyGtYa%2Fb7USGUZ2N9Nin9xeLpq%2Feb%2Fy%2Fl0ul093U2umN%2FwA2T8m%2FyTrCn3P3O%2F8Ael0us60%2Fi%2F8Akn%2BF6hPef7Pjf7%2BL%2BpF1JWu%2F2Opf9B%2F9aTpdLoB49%2B%2FrNv7Zqv8AcHrtpf7D1P8AV5P%2FADp1%2F9k%3D"
        
        // generate link
        var generatedURL = addUserURL

        generatedURL.append("?username=" + parameters["username"]!)
        generatedURL.append("&password=" + parameters["password"]!)
        generatedURL.append("&real_name=" + parameters["real_name"]!)
        generatedURL.append("&b64_jpg=" + parameters["b64_jpg"]!)
        
        // make server call
        //Alamofire.request(method: .post, parameters: parameters).responseJSON { response in
        Alamofire.request(generatedURL, method: .post).responseJSON { response in
            if response.result.isSuccess {
                // check if the server call was a success
                let returned_data = JSON(response.result.value!)
                
                // proccess the returned data from server
                switch returned_data["result"].string! {
                case "success":
                    
                    let user_data = returned_data["account"]
                    if user_data != JSON.null {
                        currentUser = Player(data: user_data)
                        currentUser?.printDebugInfo()
                        
                        // go to main menu
                        self.goToMainMenu()
                    }
                    return
                case "exists":
                    self.popUpAlert(title: "Error", message: "user \"\(parameters["username"])\" already exits.", handler: nil)
                    return
                default:
                    self.popUpAlert(title: "Error", message: "Server Error: \(returned_data["result"].string!)", handler: nil)
                    return
                }
            } else {
                self.popUpAlert(title: "Server Error", message: "There was an error connecting to the server.", handler: nil)
            }
        
        }
        
        /* OLD CODE:
         
         print("\n\nResponse String: \(response.result.value!)")
         
         if response.result.isSuccess {
         let server_data = JSON(response.result.value!)
         print(server_data)
         
         if server_data["result"].string == "exists" {
         self.popUpAlert(title: "Error", message: "User already exits", handler: nil)
         return
         }
         
         let user_data = server_data["account"]
         
         print(user_data)
         
         currentUser = Player(data: user_data)
         self.goToMainMenu()
         
         } else {
         self.popUpAlert(title: "Error", message: "There was an issue creating your account", handler: nil)
         }
        */
        
    }
 
    @IBAction func showLoginPage(_ sender: AnyObject) {
        self.navigationController!.popViewController(animated: true)
    }
 
    func goToMainMenu() {
        performSegue(withIdentifier: "createToMenu", sender: nil)
    }
 
    // MARK: User Icon Methods
 
    @IBAction func openCameraButton(_ sender: UIButton) {
        if UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.camera) {
            let imagePicker = UIImagePickerController()
            imagePicker.delegate = self
            imagePicker.sourceType = UIImagePickerControllerSourceType.camera
            imagePicker.allowsEditing = true
            self.present(imagePicker, animated: true, completion: nil)
        } else {
            popUpAlert(title: "No Camera Detected", message: "Sorry, but there was not camera detected.", handler: nil)
        }
    }
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : Any]) {
        userIcon.image = info["UIImagePickerControllerEditedImage"] as! UIImage?
        self.dismiss(animated: true, completion: nil);
    }
    
    // MARK: Text Field Delegate and Other Methods
    
    // sets up a text field
    func prepareTextField(textField:UITextField, secure:Bool, capitalType:UITextAutocapitalizationType?){
        textField.delegate = self
        textField.text = ""
        
        if secure {
            textField.isSecureTextEntry = true
        } else {
            textField.autocorrectionType = .no
            if capitalType != nil {
                textField.autocapitalizationType = capitalType!
            }
        }
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        
        textField.resignFirstResponder()
        return true
    }
}
