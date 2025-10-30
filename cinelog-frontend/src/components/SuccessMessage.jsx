import React from 'react';
import { CheckCircle, X } from 'lucide-react';

const SuccessMessage = ({ message, onClose }) => {
  if (!message) return null;

  return (
    <div className="bg-green-500/10 border border-green-500 text-green-500 px-4 py-3 rounded-lg mb-4 flex items-center justify-between animate-slide-up">
      <div className="flex items-center">
        <CheckCircle className="w-5 h-5 mr-2" />
        <span>{message}</span>
      </div>
      {onClose && (
        <button
          onClick={onClose}
          className="text-green-500 hover:text-green-400 transition-colors"
        >
          <X className="w-5 h-5" />
        </button>
      )}
    </div>
  );
};

export default SuccessMessage;