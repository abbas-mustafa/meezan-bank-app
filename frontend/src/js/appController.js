/**
 * @license
 * Copyright (c) 2014, 2025, Oracle and/or its affiliates.
 * Licensed under The Universal Permissive License (UPL), Version 1.0
 * as shown at https://oss.oracle.com/licenses/upl/
 * @ignore
 */
/*
 * Meezan Bank - Application Controller
 */
define(['knockout', 'ojs/ojcontext', 'ojs/ojcorerouter', 'ojs/ojmodulerouter-adapter', 'ojs/ojknockoutrouteradapter', 'ojs/ojurlparamadapter', 'ojs/ojmodule-element', 'ojs/ojknockout'],
  function(ko, Context, CoreRouter, ModuleRouterAdapter, KnockoutRouterAdapter, UrlParamAdapter) {

     function ControllerViewModel() {
        // Navigation data - simple routes without UI elements
        let navData = [
          { path: '', redirect: 'login' },
          { path: 'login', detail: { label: 'Login' } },
          { path: 'forgot-password', detail: { label: 'Forgot Password' } }
        ];

        // Router setup
        let router = new CoreRouter(navData, {
          urlAdapter: new UrlParamAdapter()
        });
        router.sync();

        this.moduleAdapter = new ModuleRouterAdapter(router);
        this.selection = new KnockoutRouterAdapter(router);

        // Store current route for potential use
        this.currentRoute = ko.pureComputed(() => {
          return this.selection.selectedState()?.path || 'login';
        });
     }

     // Release the application bootstrap busy state
     Context.getPageContext().getBusyContext().applicationBootstrapComplete();

     return new ControllerViewModel();
  }
);