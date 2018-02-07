/*
 * Copyright (C) 2015, 2016  Green Screens Ltd.
 */


/**
 * Include fingerprint lib in web page and this script to generate  fingerprint
 * http://localhost:9080/lite/lib/client.min.js
 */
	
function getFingerprint() {

    var cli = new ClientJS();

    var args = [];
    var data = {};
    var keys = ['isFont', 'getBrowserData', 'getFonts', 'getCanvasPrint', 'getMimeTypes',
      'getSoftwareVersion', 'getCustomFingerprint', 'getFingerprint', 'getPlugins',
      'isFlash', 'getFlashVersion', 'isSilverlight', 'getSilverlightVersion'
    ];

    function fill(v, i, a) {
      if (typeof cli[v] === 'function') {
        var txt = v;
        if (txt.indexOf('get') === 0) {
          txt = txt.charAt(3).toLowerCase() + txt.substr(4);
        }

        var val = cli[v]();
        if (val !== undefined) {
          data[txt] = val;
          if (txt.indexOf('finger') < 0) {
            args.push(val);
          }
        }
      }
      return true;
    }

    var itm, n, els = [];
    for (itm in cli) {
      n = '' + itm;
      if (keys.indexOf(n) < 0 && (n.indexOf('get') === 0 || n.indexOf('is') === 0)) {
        els.push(n);
      }
    }
    els.every(fill);

    var fingerprint = cli.getCustomFingerprint.call(cli, args);
    document.cookie = "fid=" + fingerprint;
    return fingerprint;
  }