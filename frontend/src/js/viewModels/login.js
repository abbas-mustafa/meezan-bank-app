define(['knockout', 'ojs/ojcontext', 'ojs/ojcorerouter', 'jsencrypt', 'config'], 
  function(ko, Context, CoreRouter, JSEncrypt, config) {
    
    function LoginViewModel(params) {
      const self = this;
      const { router } = params || {};

      // --- Observables ---
      self.username = ko.observable("");
      self.password = ko.observable("");
      self.rememberMe = ko.observable(false);
      self.isLoading = ko.observable(false);
      self.showPassword = ko.observable(false);

      // --- Computed Properties ---
      self.isFormValid = ko.computed(function () {
        if (self.isLoading()) {
            return false;
        }
        return self.username().trim() !== "" && self.password().trim() !== "";
      });

      self.passwordFieldType = ko.computed(function() {
        return self.showPassword() ? 'text' : 'password';
      });

      self.passwordToggleText = ko.computed(function() {
        return self.showPassword() ? 'HIDE' : 'SHOW';
      });

      self.encryptPassword = function(plainTextPassword) {
        const encrypt = new JSEncrypt();
        const publicKey = `-----BEGIN PUBLIC KEY-----
        ${config.publicKey}
        -----END PUBLIC KEY-----`;
        encrypt.setPublicKey(publicKey);
        const encrypted = encrypt.encrypt(plainTextPassword);
        return encrypted;
      };
      
      // --- API Function ---
      self.loginApi = async function(username, password) {
          self.isLoading(true);
          const backendUrl = 'http://localhost:8080/api/users/login';

          try {
            let passwordToSend = password;
              if (config.CLIENT_SIDE_ENCRYPTION_ENABLED) {
                  passwordToSend = self.encryptPassword(password);
                  if (!passwordToSend) {
                      throw new Error("Encryption failed. The password may be too long for the RSA key.");
                  }
              }
              const response = await fetch(backendUrl, {
                  method: 'POST',
                  headers: { 'Content-Type': 'application/json' },
                  body: JSON.stringify({ username: username, password: passwordToSend })
              });

              if (response.status === 401) {
                  return null; 
              }

              if (!response.ok) {
                  throw new Error(`Server error: ${response.status}`);
              }
              return await response.json();

          } catch (error) {
              console.error("Login API error:", error);
              return 'CONNECTION_ERROR';
          } finally {
              self.isLoading(false);
          }
      };

      // --- Event Handlers ---
      self.togglePassword = function() {
        self.showPassword(!self.showPassword());
      };

      self.login = async function() {
        if (self.isFormValid()) {
            const result = await self.loginApi(self.username(), self.password());

            if (result === 'CONNECTION_ERROR') {
                alert('Could not connect to the login service. Please try again later.');
            } else if (result && result.data) {
                alert(`Login Successful! Welcome, ${result.data.name}.`);
                console.log("Logged in user data:", result.data);
            } else {
                alert('Invalid username or password.');
            }
        }
      };

      self.register = function() {
        console.log("Register button clicked");
        alert("Redirecting to registration page...");
      };

      self.navigateToForgot = function() {
        console.log("Navigate to forgot password clicked");
        self.username("");
        self.password("");
        
        if (router) {

          router.go({ path: 'forgot-password' });
          
        } else {
          console.error("Router not available - using fallback");
          alert("Forgot password functionality");
        }
      };

      // --- Lifecycle Methods ---
      self.connected = function() {
        console.log("Login page connected, bindings applied",config.CLIENT_SIDE_ENCRYPTION_ENABLED);
        setTimeout(function() {
          const usernameInput = document.getElementById('username');
          if (usernameInput) {
            usernameInput.focus();
          }
        }, 100);
      };

      self.disconnected = function() {
        console.log("Login page disconnected");
      };
    }

    return LoginViewModel;
  }
);