define(['knockout', 'ojs/ojcontext', 'ojs/ojcorerouter', 'jsencrypt', 'config'],
  function(ko, Context, CoreRouter, JSEncrypt, config) {

    function ForgotPasswordViewModel(params) {
      const self = this;
      const { router } = params || {};

      self.encryptPassword = function(plainTextPassword) {
        const encrypt = new JSEncrypt();
        const publicKey = `-----BEGIN PUBLIC KEY-----
        ${config.publicKey}
        -----END PUBLIC KEY-----`;
        encrypt.setPublicKey(publicKey);
        const encrypted = encrypt.encrypt(plainTextPassword);
        return encrypted;
      };

      //================================================================================
      // CORE OBSERVABLES
      //================================================================================
      self.username = ko.observable("");
      self.accountNumber = ko.observable("");
      self.cnic = ko.observable("");
      self.accountType = ko.observable("");
      self.selectedDetailOption = ko.observable("");

      self.newPassword = ko.observable("");
      self.confirmPassword = ko.observable("");

      //================================================================================
      // UI STATE OBSERVABLES
      //================================================================================
      self.currentStep = ko.observable(1);
      self.totalSteps = 4;
      self.isLoading = ko.observable(false);
      self.showResetSection = ko.observable(false);
      self.showSuccessSection = ko.observable(false);

      // Live validation for CNIC
      self.cnicStatusMessage = ko.observable("");
      self.cnicStatusColor = ko.observable("black");
      self.isCnicVerified = ko.observable(false);

      // Live validation for Account Number
      self.accountNumberStatusMessage = ko.observable("");
      self.accountNumberStatusColor = ko.observable("black");
      self.isAccountNumberVerified = ko.observable(false);
      
      // Password visibility toggles
      self.showNewPassword = ko.observable(false);
      self.showConfirmPassword = ko.observable(false);


      //================================================================================
      // COMPUTED PROPERTIES (Derived from Observables)
      //================================================================================

      self.formattedCNIC = ko.computed({
        read: function() {
          let raw = self.cnic().replace(/\D/g, "");
          if (raw.length > 5 && raw.length <= 12) {
            return raw.slice(0, 5) + "-" + raw.slice(5);
          } else if (raw.length > 12) {
            return raw.slice(0, 5) + "-" + raw.slice(5, 12) + "-" + raw.slice(12, 13);
          }
          return raw;
        },
        write: function(value) {
          self.cnic(value.replace(/\D/g, ""));
        }
      });

      self.formattedAccountNumber = ko.computed({
        read: function() {
          let raw = self.accountNumber().replace(/\D/g, "").slice(0, 14);
          if (raw.length > 5) {
            return raw.slice(0, 5) + '-' + raw.slice(5);
          }
          return raw;
        },
        write: function(value) {
          self.accountNumber(value.replace(/\D/g, ""));
        }
      });

      self.showCNICSection = ko.computed(() => self.currentStep() === 1 && self.accountType() === "Individual");
      self.showAccountNumberSection = ko.computed(() => self.selectedDetailOption() === "account-number");
      self.showGlobalButtons = ko.computed(() => self.currentStep() !== self.totalSteps && !self.showSuccessSection() && !self.showResetSection());
      self.showStepperBar = ko.computed(() => !self.showSuccessSection());

      self.isNextButtonEnabled = ko.computed(function() {
        if (self.isLoading()) return false;

        const step = self.currentStep();
        if (step === 1) {
            const type = self.accountType();
            if (type === "Individual") {
                return self.cnic().replace(/\D/g, '').length === 13 && self.isCnicVerified();
            }
            return type && type !== "Sole Proprietor" && type !== "Foreign National";
        }
        if (step === 2) {
            if (self.selectedDetailOption() === "account-number") {
                return self.accountNumber().replace(/\D/g, '').length === 14 && self.isAccountNumberVerified();
            }
            return false;
        }
        return false;
      });

      self.passwordStrength = ko.computed(function() {
        const val = self.newPassword();
        let strength = 0;
        if (val.length > 0) strength = 1;
        const hasNumbers = /[0-9]/.test(val);
        const hasLetters = /[a-zA-Z]/.test(val);
        const hasSpecial = /[^a-zA-Z0-9]/.test(val);

        if (val.length >= 8 && hasNumbers && hasLetters) strength = 3;
        else if (val.length >= 6) strength = 2;

        if (val.length >= 8 && hasNumbers && hasLetters && hasSpecial) strength = 4;
        if (strength === 4 && val.length >= 12) strength = 5;

        return strength;
      });

      self.passwordStrengthText = ko.computed(() => ["", "Weak", "Easy", "Medium", "Strong", "Very Strong"][self.passwordStrength()] || "");
      self.passwordStrengthColor = ko.computed(() => ["", "red", "orange", "goldenrod", "green", "#0b7a0b"][self.passwordStrength()] || "");
      
      self.strengthBars = ko.computed(function() {
        const strength = self.passwordStrength();
        const colors = ["#ccc", "red", "orange", "goldenrod", "green", "#0b7a0b"];
        const bars = [];
        for (let i = 0; i < 5; i++) {
          bars.push({ color: i < strength ? colors[strength] : "#ccc" });
        }
        return bars;
      });

      self.passwordsMatch = ko.computed(() => self.newPassword() === self.confirmPassword() && self.newPassword().length > 0);
      self.passwordMatchText = ko.computed(() => self.confirmPassword().length === 0 ? "" : (self.passwordsMatch() ? "Password Matched!" : "Passwords do not match"));
      self.passwordMatchColor = ko.computed(() => self.confirmPassword().length === 0 ? "" : (self.passwordsMatch() ? "green" : "red"));
      self.isUpdatePasswordEnabled = ko.computed(() => self.passwordStrength() >= 3 && self.passwordsMatch());

      self.updatePasswordButtonStyle = ko.computed(function() {
        const opacity = self.isUpdatePasswordEnabled() ? '1' : '0.5';
        const cursor = self.isUpdatePasswordEnabled() ? 'pointer' : 'not-allowed';
        return `opacity: ${opacity}; cursor: ${cursor};`;
      });

      self.nextButtonStyle = ko.computed(function() {
        const opacity = self.isNextButtonEnabled() ? '1' : '0.5';
        const cursor = self.isNextButtonEnabled() ? 'pointer' : 'not-allowed';
        return `opacity: ${opacity}; cursor: ${cursor};`;
      });
      
      self.newPasswordType = ko.computed(() => self.showNewPassword() ? 'text' : 'password');
      self.confirmPasswordType = ko.computed(() => self.showConfirmPassword() ? 'text' : 'password');
      self.newPasswordToggleText = ko.computed(() => self.showNewPassword() ? 'HIDE' : 'SHOW');
      self.confirmPasswordToggleText = ko.computed(() => self.showConfirmPassword() ? 'HIDE' : 'SHOW');

      //================================================================================
      // API FUNCTIONS
      //================================================================================

      self.verifyCnicApi = async function(cnicNumber) {
        self.isLoading(true);
        const backendUrl = 'http://localhost:8080/api/account-flow/verify-cnic';
        try {
          const response = await fetch(backendUrl, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ cnicNo: cnicNumber })
          });
          if (response.status === 404) return null;
          if (!response.ok) throw new Error(`Server responded with status: ${response.status}`);
          return await response.json();
        } catch (error) {
          console.error("Error verifying CNIC:", error);
          return 'CONNECTION_ERROR';
        } finally {
          self.isLoading(false);
        }
      };

      self.verifyAccountApi = async function(accountNumber, cnicNo) {
        self.isLoading(true);
        const backendUrl = 'http://localhost:8080/api/account-flow/verify-account';
        try {
          const response = await fetch(backendUrl, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ accountNumber, cnicNo })
          });
          if (response.status === 404) return null;
          if (!response.ok) throw new Error(`Server responded with status: ${response.status}`);
          return await response.json();
        } catch (error) {
          console.error("Error verifying account:", error);
          return 'CONNECTION_ERROR';
        } finally {
          self.isLoading(false);
        }
      };

      self.resetPasswordApi = async function(cnicNo, newPassword) {
        self.isLoading(true);
        const backendUrl = 'http://localhost:8080/api/account-flow/reset-password';
        try {
          const response = await fetch(backendUrl, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ cnicNo, newPassword })
          });
          if (response.status === 404) {
            alert("User not found. Could not reset password.");
            return null;
          }
          if (!response.ok) throw new Error(`Server responded with status: ${response.status}`);
          return await response.json();
        } catch (error) {
          console.error("Error resetting password:", error);
          alert("An error occurred while resetting your password. Please try again.");
          return null;
        } finally {
          self.isLoading(false);
        }
      };



      
      //================================================================================
      // UI EVENT HANDLERS & NAVIGATION
      //================================================================================
      
      self.selectAccount = (type) => self.accountType(type);
    //   self.selectAccount = function(type) {
    //     if (self.accountType() !== type) {
    //         self.accountType(type);
            
    //         self.cnic("");
    //         self.cnicStatusMessage("");
    //         self.isCnicVerified(false);
    //     }
    // };
      self.isAccountSelected = (type) => self.accountType() === type;
      self.selectDetailOption = (option) => self.selectedDetailOption(option);

    //   self.selectDetailOption = function(option) {
    //     // Only clear the fields if the user is selecting a different option
    //     if (self.selectedDetailOption() !== option) {
    //         // Set the new option
    //         self.selectedDetailOption(option);
            
    //         // Clear the account number input and its validation status
    //         self.accountNumber("");
    //         self.accountNumberStatusMessage("");
    //         self.isAccountNumberVerified(false);
    //     }
    // };
      self.isDetailSelected = (option) => self.selectedDetailOption() === option;
      self.toggleNewPassword = () => self.showNewPassword(!self.showNewPassword());
      self.toggleConfirmPassword = () => self.showConfirmPassword(!self.showConfirmPassword());
      self.showResetPassword = () => self.showResetSection(true);

      self.backToUsername = () => {
          self.showResetSection(false);
          self.newPassword("");
          self.confirmPassword("");
      };

      self.updatePassword = async function() {
        if (self.isUpdatePasswordEnabled()) {
          const rawCNIC = self.cnic().replace(/\D/g, '');
          let passwordToSend = self.newPassword();
          if (config.CLIENT_SIDE_ENCRYPTION_ENABLED) {
              passwordToSend = self.encryptPassword(passwordToSend);
              if (!passwordToSend) {
                  alert("Encryption failed. The new password may be too long.");
                  return;
              }
          }
          const updatedUserResponse = await self.resetPasswordApi(rawCNIC, passwordToSend);
          if (updatedUserResponse && updatedUserResponse.data) {
            console.log("Password updated successfully for user:", updatedUserResponse.data.username);
            self.showResetSection(false);
            self.showSuccessSection(true);
          }
        }
      };

      self.navigateToLogin = function() {
        if (router) router.go({ path: 'login' });
        else alert("Navigate to login page");
      };
      
      self.continueToLogin = self.navigateToLogin;
      self.continueFromSuccess = self.navigateToLogin;

      self.nextStep = function() {
        if (self.isNextButtonEnabled()) {
          const current = self.currentStep();
          if (current === 1) self.currentStep(2);
          else if (current === 2) self.currentStep(4);
        }
      };

      self.backStep = function() {
        const current = self.currentStep();
        // if (current === 4) self.currentStep(2);
        if ((current > 1) && (current != 2))
          {
            self.currentStep(current - 1);
          }
        else if(current === 2)
        {
          // self.selectedDetailOption("");
          // self.accountNumber("");
          self.currentStep(current - 1);
        }
        else
          {
            self.navigateToLogin();
          }
      };

      //================================================================================
      // LIFECYCLE & DOM METHODS
      //================================================================================

      self.getStepClass = function(stepNumber) {
        const current = self.currentStep();
        if (stepNumber < current) return 'completed';
        if (stepNumber === current) return 'active';
        return '';
      };

      self.isStepVisible = function(stepNumber) {
        return self.currentStep() === stepNumber;
      };

      self.updateStepperUI = function() {
        const currentStep = self.currentStep();
        setTimeout(function() {
          document.querySelectorAll('.step-item').forEach((item, index) => {
            const stepCircle = item.querySelector('.step');
            const stepText = item.querySelector('p');
            const stepLine = item.querySelector('.step-line');

            if (index + 1 < currentStep) {
              item.classList.add('completed');
              item.classList.remove('active');
              if (stepCircle) {
                stepCircle.style.background = "#2ec22e";
                stepCircle.style.borderColor = "#2ec22e";
                stepCircle.style.color = "#fff";
              }
              if (stepLine) stepLine.style.background = "#2ec22e";
              if (stepText) stepText.style.color = "#2ec22e";
            } else if (index + 1 === currentStep) {
              item.classList.add('active');
              item.classList.remove('completed');
              if (stepCircle) {
                stepCircle.style.background = "#efefef";
                stepCircle.style.borderColor = "#555";
                stepCircle.style.color = "#000";
              }
              if (stepLine) stepLine.style.background = "#ccc";
              if (stepText) stepText.style.color = "#555";
            } else {
              item.classList.remove('active', 'completed');
              if (stepCircle) {
                stepCircle.style.background = "#efefef";
                stepCircle.style.borderColor = "#555";
                stepCircle.style.color = "#000";
              }
              if (stepLine) stepLine.style.background = "#ccc";
              if (stepText) stepText.style.color = "#555";
            }
          });
        }, 10);
      };

      self.connected = function() {

        self.cnic.subscribe(async function(newValue) {
          const rawCNIC = (newValue || "").replace(/\D/g, '');
          self.accountNumber("");
          self.accountNumberStatusMessage("");
          self.isAccountNumberVerified(false);
          self.isCnicVerified(false); 
          if (rawCNIC.length < 13) {
            self.cnicStatusMessage("");
            return;
          }
          if (rawCNIC.length === 13) {
            self.cnicStatusMessage("Checking...");
            self.cnicStatusColor("black");
            const userResponse = await self.verifyCnicApi(rawCNIC);
            if (userResponse === 'CONNECTION_ERROR') {
              self.cnicStatusMessage("Verification service is unavailable. Please try again later.");
              self.cnicStatusColor("red");
              self.isCnicVerified(false);
            } else if (userResponse && userResponse.data) {
              self.cnicStatusMessage("CNIC verified successfully.");
              self.cnicStatusColor("green");
              self.isCnicVerified(true);
              self.username(userResponse.data.username);
            } else {
              self.cnicStatusMessage("This CNIC is not registered with us. Please check the number and try again.");
              self.cnicStatusColor("red");
              self.isCnicVerified(false);
            }
          }
        });
        
        self.accountNumber.subscribe(async function(newValue) {
          const rawAccountNumber = (newValue || "").replace(/\D/g, '');
          self.isAccountNumberVerified(false);
          if (rawAccountNumber.length < 14) {
            self.accountNumberStatusMessage("");
            return;
          }
          if (rawAccountNumber.length === 14) {
            self.accountNumberStatusMessage("Verifying account...");
            self.accountNumberStatusColor("black");
            const rawCNIC = self.cnic().replace(/\D/g, '');
            const accountResponse = await self.verifyAccountApi(rawAccountNumber, rawCNIC);
            if (accountResponse === 'CONNECTION_ERROR') {
              self.accountNumberStatusMessage("Service is unavailable. Please try again later.");
              self.accountNumberStatusColor("red");
              self.isAccountNumberVerified(false);
            } else if (accountResponse) {
              self.accountNumberStatusMessage("Account verified successfully.");
              self.accountNumberStatusColor("green");
              self.isAccountNumberVerified(true);
            } else {
              self.accountNumberStatusMessage("This account number does not match your CNIC.");
              self.accountNumberStatusColor("red");
              self.isAccountNumberVerified(false);
            }
          }
        });

        self.currentStep.subscribe(self.updateStepperUI);
        
        self.updateStepperUI();
      };

      self.disconnected = function() {
      };
    }

    return ForgotPasswordViewModel;
  }
);